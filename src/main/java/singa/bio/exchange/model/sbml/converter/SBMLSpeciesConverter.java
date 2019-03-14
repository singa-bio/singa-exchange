package singa.bio.exchange.model.sbml.converter;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Species;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.sbml.SBMLParser;
import singa.bio.exchange.model.sections.SubsectionCache;

import java.util.regex.Matcher;

import static singa.bio.exchange.model.sbml.SBMLParser.DEFAULT_SBML_ORIGIN;

/**
 * @author cl
 */
public class SBMLSpeciesConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLSpeciesConverter.class);

    public static void convert(ListOf<Species> listOfSpecies) {
        SBMLSpeciesConverter converter = new SBMLSpeciesConverter();
        converter.convertSpeciesList(listOfSpecies);
        for (ChemicalEntity chemicalEntity : EntityCache.getAll()) {
            SBMLParser.current.addReferencedEntity(chemicalEntity);
        }
        converter.convertInitialConcentrations(listOfSpecies);
    }

    private void convertSpeciesList(ListOf<Species> listOfSpecies) {
        logger.info("Parsing chemical entity data ...");
        for (Species species : listOfSpecies) {
            logger.debug("Parsing entity {} ...", species.getId());
//            boolean isComplex = false;
//            for (CVTerm cVTerm : species.getAnnotation().getListOfCVTerms()) {
//                if (cVTerm.getQualifier() == CVTerm.Qualifier.BQB_HAS_PART) {
//                    isComplex = true;
//                    break;
//                }
//            }
//            if (isComplex) {
//                ComplexEntity complex = new ComplexEntity.create(species.getId())
//                        .name(species.getName())
//                        .build();
//                for (CVTerm term : species.getAnnotation().getListOfCVTerms()) {
//                    if (term.getQualifier() == CVTerm.Qualifier.BQB_HAS_PART) {
//                        complex.addAssociatedPart(createComponent(term));
//                    }
//                }
//                EntityCache.add(complex);
//            } else {
                for (CVTerm term : species.getAnnotation().getListOfCVTerms()) {
                    if (term.getQualifier() == CVTerm.Qualifier.BQB_IS || term.getQualifier() == CVTerm.Qualifier.BQB_IS_VERSION_OF) {
                        ChemicalEntity entity = createEntity(species.getId(), term);
                        entity.setName(species.getName());
                        EntityCache.add(entity);
                        break;
                    }
                }
//            }
            if (EntityCache.get(species.getId()) == null) {
                ChemicalEntity entity = SmallMolecule.create(species.getId()).build();
                entity.setName(species.getName());
                EntityCache.add(entity);
            }
        }

    }

    private ChemicalEntity createEntity(String primaryIdentifier, CVTerm term) {
        for (String resource : term.getResources()) {
            // try to parse as ChEBI
            Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
            if (matcherChEBI.find()) {
                return SmallMolecule.create(primaryIdentifier)
                        .assignFeature(new ChEBIIdentifier(matcherChEBI.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
            // try to parse as UniProt
            Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
            if (matcherUniProt.find()) {
                return new Protein.Builder(primaryIdentifier)
                        .assignFeature(new UniProtIdentifier(matcherUniProt.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
        }
        // no parser available
        return SmallMolecule.create(primaryIdentifier)
                .build();
    }

    private ChemicalEntity createComponent(CVTerm term) {
        for (String resource : term.getResources()) {
            // try to parse as ChEBI
            Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
            if (matcherChEBI.find()) {
                return SmallMolecule.create(matcherChEBI.group(0))
                        .assignFeature(new ChEBIIdentifier(matcherChEBI.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
            // try to parse as UniProt
            Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
            if (matcherUniProt.find()) {
                return new Protein.Builder(matcherUniProt.group(0))
                        .assignFeature(new UniProtIdentifier(matcherUniProt.group(0), DEFAULT_SBML_ORIGIN))
                        .build();
            }
        }
        // no parser available
        return SmallMolecule.create(term.getResource(0))
                .build();
    }

    public void convertInitialConcentrations(ListOf<Species> listOfSpecies) {
        ConcentrationInitializer ci = new ConcentrationInitializer();
        for (Species species : listOfSpecies) {
            ChemicalEntity entity = EntityCache.get(species.getId());
            CellSubsection subsection = SubsectionCache.get(species.getCompartment());
            ci.addInitialConcentration(subsection, entity, UnitRegistry.concentration(species.getInitialConcentration()));
        }
        SBMLParser.current.setConcentrationInitializer(ci);
    }

}
