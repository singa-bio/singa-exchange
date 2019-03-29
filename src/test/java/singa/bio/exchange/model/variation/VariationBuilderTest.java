package singa.bio.exchange.model.variation;

import bio.singa.chemistry.features.reactions.SecondOrderForwardsRateConstant;
import bio.singa.chemistry.features.reactions.SecondOrderRate;
import bio.singa.core.utility.Resources;
import bio.singa.simulation.features.variation.ConcentrationVariation;
import bio.singa.simulation.features.variation.ModuleFeatureVariation;
import bio.singa.simulation.features.variation.VariationSet;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.SimulationRepresentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
class VariationBuilderTest {

    @Test
    void entityVariation() throws IOException {

        String fileLocation = Resources.getResourceAsFileLocation("pka_regulation.json");
        Simulation simulation;

        simulation = Converter.getSimulationFrom(String.join("", Files.readAllLines(Paths.get(fileLocation))));

        ConcentrationVariation campVariation = VariationBuilder.entityVariation()
                .entity("CAMP")
                .subsection("cytoplasm")
                .everywhere()
                .concentrations(0.5, 1.0, 1.5, 2.0)
                .unit(MICRO_MOLE_PER_LITRE)
                .build();

        ConcentrationVariation pkaiVariation = VariationBuilder.entityVariation()
                .entity("(AKAP:(PKAC:(PKAR:APS)))")
                .subsection("vesicle membrane")
                .everywhere()
                .concentrations(0.1, 0.2, 0.4)
                .unit(MICRO_MOLE_PER_LITRE)
                .build();

        ConcentrationVariation pdeVariation = VariationBuilder.entityVariation()
                .entity("(PDE4:PS)")
                .subsection("vesicle membrane")
                .everywhere()
                .concentrations(0.1, 0.2, 0.4)
                .unit(MICRO_MOLE_PER_LITRE)
                .build();

        ModuleFeatureVariation rateVariation = VariationBuilder.featureVariation(simulation)
                .module("protein kinase a activation: camp pocket a binding")
                .featureClass(SecondOrderForwardsRateConstant.class)
                .quantityValues(4000.0, 20000.0, 200000.0)
                .unit(LITRE.divide(SECOND.multiply(MOLE)), SecondOrderRate.class)
                .build();

        VariationSet variations = new VariationSet();
        variations.addAll(campVariation, pkaiVariation, pdeVariation, rateVariation);

        SimulationRepresentation representation = Converter.getRepresentationFrom(simulation);
        VariationGenerator.attachAlternativeValue(representation, variations);
        System.out.println(representation.toJson());

        VariationSet variationSet = VariationGenerator.generateVariationSet(representation);
        System.out.println(variationSet);

    }

}