package singa.bio.exchange.model.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StaticReactantRepresentation.class, name = "static"),
        @JsonSubTypes.Type(value = DynamicSubstrateRepresentation.class, name = "dynamic-substrate"),
        @JsonSubTypes.Type(value = DynamicProductRepresentation.class, name = "dynamic-product")
})
public abstract class ReactantRepresentation {



}
