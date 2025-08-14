package dev.shaaf.kantra.rules.gen.builtin.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;

public record BuiltinFileCondition(@JsonProperty("builtin.file") BuiltinFile details) implements Condition {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinFile(String pattern) {}
}