package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Ruleset(
        String name,
        String description,
        List<String> labels,
        List<String> tags,
        List<Rule> rules
) {}
