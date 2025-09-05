package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Rule(
        @JsonProperty("ruleID") String ruleId,
        String message,
        String description,
        Category category,
        Integer effort,
        List<String> labels,
        List<String> tag,
        @JsonProperty("customVariable") List<CustomVariable> customVariables,
        Condition when
) {}
