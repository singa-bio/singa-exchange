package bio.singa.exchange.sbml;

import bio.singa.core.parser.AbstractHTMLParser;
import bio.singa.simulation.model.modules.UpdateModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SabioRKParserService extends AbstractHTMLParser<List<UpdateModule>> {

    private static final String SABIORK_FETCH_URL = "http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/sbml";
    private final Map<String, String> queryMap;

    public SabioRKParserService(String entryID) {
        setResource(SABIORK_FETCH_URL);
        queryMap = new HashMap<>();
        queryMap.put("q", entryID);
    }

    @Override
    public List<UpdateModule> parse() {
        fetchWithQuery(queryMap);
        SBMLParser parser = new SBMLParser(getFetchResult());
        parser.parse();
        return SBMLParser.current.getModules();

    }


}
