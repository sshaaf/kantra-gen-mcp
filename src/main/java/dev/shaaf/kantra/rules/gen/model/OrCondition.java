package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrCondition(
    @JsonProperty("or") List<Condition> conditions,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public OrCondition(List<Condition> conditions) {
        this(conditions, null, null, null, null);
    }
}
