package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JavaReferenced.class, name = "java.referenced"),
        @JsonSubTypes.Type(value = JavaDependency.class, name = "java.dependency"),
})
public sealed interface WhenNode permits JavaReferenced, JavaDependency, OrNode, AndNode, ScopedNode {

}
