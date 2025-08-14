package dev.shaaf.kantra.rules.gen.builtin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;
import java.util.List;

public record BuiltinHasTagsCondition(@JsonProperty("builtin.hasTags") List<String> tags) implements Condition {
}