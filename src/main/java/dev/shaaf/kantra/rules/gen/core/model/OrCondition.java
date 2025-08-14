package dev.shaaf.kantra.rules.gen.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrCondition(@JsonProperty("or") List<Condition> conditions) implements Condition {
}