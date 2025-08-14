package dev.shaaf.kantra.rules.gen.java.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;

public record JavaDependencyCondition(@JsonProperty("java.dependency") JavaDependency details) implements Condition {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JavaDependency(
            String name,
            String nameRegex,
            String upperbound,
            String lowerbound
    ) {}
}