package dev.shaaf.kantra.rules.gen.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotCondition(@JsonProperty("not") boolean not, Condition condition) implements Condition {
}
