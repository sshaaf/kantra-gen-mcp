package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AndCondition(
    List<Condition> conditions,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public AndCondition(List<Condition> conditions) {
        this(conditions, null, null, null, null);
    }
    
    @JsonCreator
    public static AndCondition fromArray(List<Condition> conditions) {
        return new AndCondition(conditions);
    }
    
    @JsonValue
    public List<Condition> getConditions() {
        return conditions;
    }
}
