package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record KantraRule(
        @JsonProperty("ruleID") String id,
        String description,
        String message,
        Category category,
        Integer effort,
        List<String> labels,
        List<Link> links,
        @JsonProperty("customVariables") List<CustomVariable> customVariables,
        WhenNode when
) {}