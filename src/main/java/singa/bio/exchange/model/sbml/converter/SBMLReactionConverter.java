package singa.bio.exchange.model.sbml.converter;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.DynamicKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.SpeciesReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Reaction currentReaction;

    public static void convert(ListOf<org.sbml.jsbml.Reaction> listOfReactions) {
        SBMLReactionConverter converter = new SBMLReactionConverter();
        converter.convertReactionList(listOfReactions);
    }

    public List<Reaction> convertReactionList(ListOf<org.sbml.jsbml.Reaction> sbmlReactions) {
        List<Reaction> reactions = new ArrayList<>();
        for (org.sbml.jsbml.Reaction reaction : sbmlReactions) {
            reactions.add(convertReaction(reaction));
        }
        return reactions;
    }

    public Reaction convertReaction(org.sbml.jsbml.Reaction reaction) {
        logger.debug("Parsing Reaction {} ...", reaction.getName());
        currentReaction = new ReactionBuilder.GeneralReactionBuilder(SBMLParser.current)
                .identifier(reaction.getId())
                .build();

        currentReaction.setKineticLaw(SBMLFunctionConverter.convertKineticLaw(reaction.getKineticLaw().getMath(),
                reaction.getKineticLaw().getListOfLocalParameters(), currentReaction));

        assignSubstrates(reaction.getListOfReactants());
        assignProducts(reaction.getListOfProducts());
        assignModifiers(reaction.getListOfModifiers());
        logger.debug("Parsed reaction: {}", currentReaction.getKineticLaw());
        return currentReaction;
    }

    private void assignSubstrates(ListOf<SpeciesReference> substrates) {
        for (SpeciesReference reference : substrates) {
            logger.debug("Assigning entity {} as substrate.", reference.getSpecies());
            String identifier = reference.getSpecies();
            Reactant reactant = new Reactant(EntityRegistry.get(identifier), ReactantRole.SUBSTRATE, reference.getStoichiometry());
            ((DynamicKineticLaw) currentReaction.getKineticLaw()).referenceReactant(identifier, reactant);
            currentReaction.getReactantBehavior().addReactant(reactant);
        }
    }

    private void assignProducts(ListOf<SpeciesReference> products) {
        for (SpeciesReference reference : products) {
            logger.debug("Assigning entity {} as product.", reference.getSpecies());
            String identifier = reference.getSpecies();
            Reactant reactant = new Reactant(EntityRegistry.get(identifier), ReactantRole.PRODUCT, reference.getStoichiometry());
            ((DynamicKineticLaw) currentReaction.getKineticLaw()).referenceReactant(identifier, reactant);
            currentReaction.getReactantBehavior().addReactant(reactant);
        }
    }

    private void assignModifiers(ListOf<ModifierSpeciesReference> modifiers) {
        for (ModifierSpeciesReference reference : modifiers) {
            logger.debug("Assigning entity {} as catalyst.", reference.getSpecies());
            String identifier = reference.getSpecies();
            Reactant reactant = new Reactant(EntityRegistry.get(identifier), ReactantRole.CATALYTIC);
            ((DynamicKineticLaw) currentReaction.getKineticLaw()).referenceReactant(identifier, reactant);
            currentReaction.getReactantBehavior().addReactant(reactant);
        }
    }

}
