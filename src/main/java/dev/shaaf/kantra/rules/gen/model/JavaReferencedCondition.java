package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JavaReferencedCondition(
    @JsonProperty("java.referenced") JavaReferencedDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public JavaReferencedCondition(JavaReferencedDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JavaReferencedDetails(
        String pattern,
        String location,
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
