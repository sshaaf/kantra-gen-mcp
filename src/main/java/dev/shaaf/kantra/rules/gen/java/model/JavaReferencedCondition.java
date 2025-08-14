package dev.shaaf.kantra.rules.gen.java.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;

import java.util.List;

public record JavaReferencedCondition(@JsonProperty("java.referenced") JavaReferenced details) implements Condition {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JavaReferenced(
            String location,
            String pattern,
            Annotated annotated,
            List<String> filepaths
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Annotated(
            String pattern,
            List<Element> elements
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Element(
            String name,
            String value
    ) {}
}
