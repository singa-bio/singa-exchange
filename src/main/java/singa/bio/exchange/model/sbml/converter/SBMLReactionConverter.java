package singa.bio.exchange.model.sbml.converter;

import bio.singa.simulation.model.modules.concentration.imlementations.DynamicReaction;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.sbml.SBMLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts JSBML Reactions to SiNGA Reactions
 *
 * @author cl
 */
public class SBMLReactionConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLReactionConverter.class);

    private DynamicReaction currentReaction;

    public static void convert(ListOf<Reaction> listOfReactions) {
        SBMLReactionConverter converter = new SBMLReactionConverter();
        converter.convertReactionList(listOfReactions);
    }

    public List<DynamicReaction> convertReactionList(ListOf<Reaction> sbmlReactions) {
        List<DynamicReaction> reactions = new ArrayList<>();
        for (Reaction reaction : sbmlReactions) {
            reactions.add(convertReaction(reaction));
        }
        return reactions;
    }

    public DynamicReaction convertReaction(Reaction reaction) {
        logger.debug("Parsing Reaction {} ...", reaction.getName());
        currentReaction = DynamicReaction.inSimulation(SBMLParser.current)
                .identifier(reaction.getId())
                .kineticLaw(SBMLFunctionConverter.convertKineticLaw(reaction.getKineticLaw().getMath(), reaction.getKineticLaw().getListOfLocalParameters()))
                .build();

        assignSubstrates(reaction.getListOfReactants());
        assignProducts(reaction.getListOfProducts());
        assignModifiers(reaction.getListOfModifiers());
        logger.debug("Parsed reaction: {}", currentReaction.getKineticLaw().getExpressionString());
        return currentReaction;
    }

    private void assignSubstrates(ListOf<SpeciesReference> substrates) {
        for (SpeciesReference reference : substrates) {
            logger.debug("Assigning entity {} as substrate.", reference.getSpecies());
            String identifier = reference.getSpecies();
            Reactant reactant = new Reactant(EntityCache.get(identifier), ReactantRole.SUBSTRATE, reference.getStoichiometry());
            currentReaction.getKineticLaw().referenceReactant(identifier, reactant);
            currentReaction.addReactant(reactant);
        }
    }

    private void assignProducts(ListOf<SpeciesReference> products) {
        for (SpeciesReference reference : products) {
            logger.debug("Assigning entity {} as product.", reference.getSpecies());
            String identifier = reference.getSpecies();
            Reactant reactant = new Reactant(EntityCache.get(identifier), ReactantRole.PRODUCT, reference.getStoichiometry());
            currentReaction.getKineticLaw().referenceReactant(identifier, reactant);
            currentReaction.addReactant(reactant);
        }
    }

    private void assignModifiers(ListOf<ModifierSpeciesReference> modifiers) {
        for (ModifierSpeciesReference reference : modifiers) {
            logger.debug("Assigning entity {} as catalyst.", reference.getSpecies());
            String identifier = reference.getSpecies();
            Reactant reactant = new Reactant(EntityCache.get(identifier), ReactantRole.CATALYTIC);
            currentReaction.getKineticLaw().referenceReactant(identifier, reactant);
            currentReaction.addReactant(reactant);
        }
    }

}
