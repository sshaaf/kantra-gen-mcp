package dev.shaaf.kantra.rules.gen.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record KantraRule(
        String ruleID,
        String description,
        String message,
        Category category,
        Integer effort,
        List<String> labels,
        List<Link> links,
        List<CustomVariable> customVariables,
        Condition when
) {}