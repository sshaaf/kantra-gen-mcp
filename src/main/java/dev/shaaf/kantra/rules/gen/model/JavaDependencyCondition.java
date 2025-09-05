package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JavaDependencyCondition(
    String name,
    @JsonProperty("name_regex") String nameRegex,
    String upperbound,
    String lowerbound,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public JavaDependencyCondition(String name, String nameRegex, String upperbound, String lowerbound) {
        this(name, nameRegex, upperbound, lowerbound, null, null, null, null);
    }
}
