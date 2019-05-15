package singa.bio.exchange.model.variation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.trajectories.TrajectoryDataset;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class ObservationExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ObservationExtractor.class);

    // https://regexr.com/4dv1u
    // matches the last " = " and saves the identifying string in group 1 and the value of the variation in group 3
    Pattern valuePattern = Pattern.compile("(.*)(\\s=\\s)(?!.\\s*=\\s)([0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?).*");

    private Map<String, String> aliases;
    private Map<String, Double> values;

    public ObservationExtractor() {
        aliases = new HashMap<>();
        values = new HashMap<>();
    }

    public void extractVariations(Path observationPath) throws IOException {
        // traverse all directories
        try (DirectoryStream<Path> observationDirectoryStream = Files.newDirectoryStream(observationPath)) {
            for (Path observationDirectoryPath: observationDirectoryStream) {
                Path variationFilePath = observationDirectoryPath.resolve("variations.log");
                if (Files.exists(variationFilePath)) {
                    for (String line : Files.readAllLines(variationFilePath)) {
                        // TODO format the variation log reproducible and easy to parse beforehand
                        Matcher matcher = valuePattern.matcher(line);
                        if (matcher.find()) {
                            String identifierString = matcher.group(1);
                            String valueString = matcher.group(3);
                            values.put(aliases.get(identifierString), Double.valueOf(valueString));
                            // System.out.println(aliases.get(identifierString) + " : " + Double.valueOf(valueString));
                        }
                    }
                    // System.out.println();
                } else {
                    logger.warn("Could not find variation.log file in "+observationDirectoryPath);
                }
                Path trajectoryFile = observationDirectoryPath.resolve("trajectory.json");
                if (Files.exists(trajectoryFile)) {
                    TrajectoryDataset trajectoryDataset = TrajectoryDataset.fromJson(String.join("", Files.readAllLines(trajectoryFile)));
                    System.out.println();
                } else {
                    logger.warn("Could not find trajectory.json file in "+observationDirectoryPath);
                }
            }
        }
    }

    public static void main(String[] args) {
        Path observationPath = Paths.get("/home/leberech/git/model-data/raw_data/simulations_non_restricted_diffusion/16/observations");
        ObservationExtractor extractor = new ObservationExtractor();
        extractor.aliases.put("Feature:  M = protein kinase a activation: camp pocket a binding F = SecondOrderForwardsRateConstant", "camp_binding");
        extractor.aliases.put("Feature:  M = dephosphorylation by PP2B: AQP2 binding F = SecondOrderForwardsRateConstant", "dephosphorylation");
        extractor.aliases.put("Feature:  M = camp regulation: cAMP to AMP catalysis by PDE4p F = TurnoverNumber", "camp_hydrolysis");
        extractor.aliases.put("Feature:  E = CAMP F = Diffusivity", "diffusivity");
        try {
            extractor.extractVariations(observationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
