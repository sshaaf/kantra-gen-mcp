package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AndCondition(
    @JsonProperty("and") List<Condition> conditions,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public AndCondition(List<Condition> conditions) {
        this(conditions, null, null, null, null);
    }
}
