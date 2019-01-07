package singa.bio.exchange.model.sbml.converter;

import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.parameters.Parameter;
import bio.singa.simulation.model.parameters.ParameterStorage;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.sbml.SBMLParser;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.SubsectionCache;
import tec.uom.se.quantity.Quantities;

import java.util.*;

/**
 * @author cl
 */
public class SBMLCompartmentConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLCompartmentConverter.class);

    private Map<String, CellSubsection> subsections;
    private List<CellRegion> regions;
    private Map<String, Parameter> parameter;

    private Map<CellSubsection, String> topology;
    private Map<CellSubsection, Double> size;
    private Set<CellSubsection> processedSubsections;

    public SBMLCompartmentConverter() {
        subsections = new HashMap<>();
        regions = new ArrayList<>();
        parameter = new HashMap<>();
        topology = new HashMap<>();
        size = new HashMap<>();
        processedSubsections = new HashSet<>();
    }

    public static void convert(ListOf<Compartment> compartments) {
        SBMLCompartmentConverter converter = new SBMLCompartmentConverter();
        converter.parseCompartments(compartments);
    }

    private void parseCompartments(ListOf<Compartment> compartments) {
        logger.info("Parsing Compartments ...");

        // handle compartments
        for (Compartment compartment : compartments) {
            CellSubsection subsection = new CellSubsection(compartment.getId());
            this.subsections.put(subsection.getIdentifier(), subsection);
            // check topology
            if (!compartment.getOutside().isEmpty()) {
                topology.put(subsection, compartment.getOutside());
            }
            // check size
            if (compartment.getSize() != 0.0) {
                size.put(subsection, compartment.getSize());
            }
        }
        // setup  regions and parameter representing region size
        for (CellSubsection subsection : subsections.values()) {
            addContainerRegion(subsection);
            CellRegion region;
            if (topology.containsKey(subsection)) {
                CellSubsection outside = subsections.get(topology.get(subsection));
                region = new CellRegion("Border " + subsection.getIdentifier() + "-" + outside.getIdentifier());
                region.addSubsection(CellTopology.INNER, subsection);
                region.addSubsection(CellTopology.OUTER, outside);
                regions.add(region);
            }
            if (size.containsKey(subsection)) {
                parameter.put(subsection.getIdentifier(), new Parameter<>(subsection.getIdentifier(),
                        Quantities.getQuantity(size.get(subsection), UnitRegistry.getSpaceUnit()), SBMLParser.DEFAULT_SBML_ORIGIN));
            }
        }

        // add to caches
        for (CellRegion region : regions) {
            RegionCache.add(region);
        }
        for (CellSubsection subsection : subsections.values()) {
            SubsectionCache.add(subsection);
        }
        for (Map.Entry<String, Parameter> entry : parameter.entrySet()) {
            ParameterStorage.add(entry.getKey(), entry.getValue());
        }

    }

    private void addContainerRegion(CellSubsection subsection) {
        CellRegion cellRegion = new CellRegion(subsection.getIdentifier());
        cellRegion.addSubsection(CellTopology.INNER, subsection);
        regions.add(cellRegion);
    }

}
