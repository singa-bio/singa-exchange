package bio.singa.exchange.sbml.converter;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntityBuilder;
import bio.singa.simulation.entities.EntityRegistry;
import bio.singa.simulation.entities.SimpleEntity;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Species;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bio.singa.exchange.sbml.SBMLParser;
import bio.singa.exchange.sections.SubsectionCache;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static bio.singa.exchange.sbml.SBMLParser.DEFAULT_SBML_ORIGIN;

/**
 * @author cl
 */
public class SBMLSpeciesConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLSpeciesConverter.class);

    public static void convert(ListOf<Species> listOfSpecies) {
        SBMLSpeciesConverter converter = new SBMLSpeciesConverter();
        converter.convertSpeciesList(listOfSpecies);
        for (ChemicalEntity chemicalEntity : EntityRegistry.getAll()) {
            SBMLParser.current.addReferencedEntity(chemicalEntity);
        }
        converter.convertInitialConcentrations(listOfSpecies);
    }

    private void convertSpeciesList(ListOf<Species> listOfSpecies) {
        logger.info("Parsing chemical entity data ...");
        for (Species species : listOfSpecies) {
            logger.debug("Parsing entity {} ...", species.getId());
                for (CVTerm term : species.getAnnotation().getListOfCVTerms()) {
                    if (term.getQualifier() == CVTerm.Qualifier.BQB_IS || term.getQualifier() == CVTerm.Qualifier.BQB_IS_VERSION_OF) {
                        ChemicalEntity entity = createEntity(species.getId(), term);
                        EntityRegistry.put(entity);
                        break;
                    }
                }
//            }
            if (EntityRegistry.get(species.getId()) == null) {
                ChemicalEntity entity = SimpleEntity.create(species.getId()).build();
                EntityRegistry.put(entity);
            }
        }

    }

    private ChemicalEntity createEntity(String primaryIdentifier, CVTerm term) {
        for (String resource : term.getResources()) {
            // try to parse as ChEBI
            Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
            if (matcherChEBI.find()) {
                return SimpleEntity.create(primaryIdentifier)
                        .assignFeature(new ChEBIIdentifier(matcherChEBI.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
            // try to parse as UniProt
            Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
            if (matcherUniProt.find()) {
                return SimpleEntity.create(primaryIdentifier)
                        .assignFeature(new UniProtIdentifier(matcherUniProt.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
        }
        // no parser available
        return SimpleEntity.create(primaryIdentifier)
                .build();
    }

    private ChemicalEntity createComponent(CVTerm term) {
        for (String resource : term.getResources()) {
            // try to parse as ChEBI
            Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
            if (matcherChEBI.find()) {
                return SimpleEntity.create(matcherChEBI.group(0))
                        .assignFeature(new ChEBIIdentifier(matcherChEBI.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
            // try to parse as UniProt
            Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
            if (matcherUniProt.find()) {
                return SimpleEntity.create(matcherUniProt.group(0))
                        .assignFeature(new UniProtIdentifier(matcherUniProt.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
        }
        // no parser available
        return SimpleEntity.create(term.getResource(0))
                .build();
    }

    public void convertInitialConcentrations(ListOf<Species> listOfSpecies) {
        List<InitialConcentration> initialConcentrations = new ArrayList<>();
        for (Species species : listOfSpecies) {
            initialConcentrations.add(ConcentrationBuilder.create()
                    .entity(EntityRegistry.get(species.getId()))
                    .subsection(SubsectionCache.get(species.getCompartment()))
                    .concentration(UnitRegistry.concentration(species.getInitialConcentration()))
                    .build());
        }
        SBMLParser.current.setConcentrations(initialConcentrations);
    }

}
