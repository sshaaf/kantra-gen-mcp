package dev.shaaf.kantra.rules.gen.builtin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;
import java.util.List;

public record BuiltinJsonCondition(@JsonProperty("builtin.json") BuiltinJson details) implements Condition {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinJson(String xpath, List<String> filepaths) {}
}