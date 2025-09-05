package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrCondition(
    List<Condition> conditions,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public OrCondition(List<Condition> conditions) {
        this(conditions, null, null, null, null);
    }
    
    @JsonCreator
    public static OrCondition fromArray(List<Condition> conditions) {
        return new OrCondition(conditions);
    }
    
    @JsonValue
    public List<Condition> getConditions() {
        return conditions;
    }
}
