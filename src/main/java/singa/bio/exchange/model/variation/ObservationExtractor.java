package singa.bio.exchange.model.variation;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.intervals.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.trajectories.TrajectoryDataset;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tech.units.indriya.unit.MetricPrefix.MILLI;
import static tech.units.indriya.unit.Units.SECOND;


/**
 * Extract Observations from trajectories-
 *
 * @author cl
 */
public class ObservationExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ObservationExtractor.class);

    // https://regexr.com/4dv1u
    // matches the last " = " and saves the identifying string in group 1 and the value of the variation in group 3
    Pattern valuePattern = Pattern.compile("(.*)(\\s=\\s)(?!.\\s*=\\s)([0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?).*");

    private Map<String, String> aliases;

    public ObservationExtractor() {
        aliases = new HashMap<>();
    }

    public Observation extract(Path observationPath, ObservationSpecifications observations) throws IOException {
        Observation observation = new Observation();
        observation.initializeParameters(aliases.values());
        observation.initializeObservations(observations.getObservations());
        Map<String, Double> parameters = new HashMap<>();
        System.out.println(observation.getHeader());
        // traverse all directories
        try (DirectoryStream<Path> observationDirectoryStream = Files.newDirectoryStream(observationPath)) {
            for (Path observationDirectoryPath : observationDirectoryStream) {
                if (Files.isDirectory(observationDirectoryPath)) {
                    Path variationFilePath = observationDirectoryPath.resolve("variations.log");
                    if (Files.exists(variationFilePath)) {
                        BufferedReader reader = Files.newBufferedReader(variationFilePath, StandardCharsets.ISO_8859_1);
                        while (reader.ready()) {
                            String line = reader.readLine();
                            Matcher matcher = valuePattern.matcher(line);
                            if (matcher.find()) {
                                String identifierString = matcher.group(1);
                                String valueString = matcher.group(3);
                                parameters.put(aliases.get(identifierString), Double.valueOf(valueString));
                            }
                        }
                    } else {
                        logger.warn("Could not find variation.log file in " + observationDirectoryPath);
                    }
                    observation.setParameters(parameters);
                    Path trajectoryFile = observationDirectoryPath.resolve("trajectory.json");
                    if (Files.exists(trajectoryFile)) {
                        TrajectoryDataset trajectoryDataset = TrajectoryDataset.fromJson(String.join("", Files.readAllLines(trajectoryFile)));
                        for (ObservationSpecification specification : observations.getObservations()) {
                            Map<Quantity<Time>, Quantity<MolarConcentration>> observedValues = specification.observe(trajectoryDataset);
                            observation.addObservations(specification.getAlias(), observedValues);
                        }
                    } else {
                        logger.warn("Could not find trajectory.json file in " + observationDirectoryPath);
                    }
                    observation.flushObservations();
                }
            }
        }
        return observation;
    }

    public static void main(String[] args) {
        List<Quantity<Time>> times = new ArrayList<>();
        List<Double> samples = Sampler.sampleMultiplicative(100, 300000, 25);
        for (Double sample : samples) {
            times.add(Quantities.getQuantity(sample, MILLI(SECOND)));
        }

        ObservationSpecifications observations = new ObservationSpecifications();
        observations.addObservation("camp_vesicle", times, "CAMP", "cytoplasm", "n(6,3)");
        observations.addObservation("camp_cytoplasm", times, "CAMP", "cytoplasm", "n(0,0)");

        Path observationPath = Paths.get("/home/leberech/git/model-data/raw_data/simulations_unrestricted_diffusion/simulation_01/observations");
        ObservationExtractor extractor = new ObservationExtractor();
        extractor.aliases.put("Feature:  M = protein kinase a activation: camp pocket a binding F = SecondOrderForwardsRateConstant", "camp_binding");
        extractor.aliases.put("Feature:  M = dephosphorylation by PP2B: AQP2 binding F = SecondOrderForwardsRateConstant", "dephosphorylation");
        extractor.aliases.put("Feature:  M = camp regulation: cAMP to AMP catalysis by PDE4p F = TurnoverNumber", "camp_hydrolysis");
        extractor.aliases.put("Feature:  E = CAMP F = Diffusivity", "diffusivity");

        try {
            Observation observation = extractor.extract(observationPath, observations);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
