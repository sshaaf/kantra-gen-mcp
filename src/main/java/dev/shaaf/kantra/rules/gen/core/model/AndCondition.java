package dev.shaaf.kantra.rules.gen.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AndCondition(@JsonProperty("and") List<Condition> conditions) implements Condition {
}