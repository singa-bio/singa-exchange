package bio.singa.exchange;

import bio.singa.exchange.units.UnitJacksonModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author cl
 */
public interface Jsonizable {

    default String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        return mapper.writeValueAsString(this);
    }

}
