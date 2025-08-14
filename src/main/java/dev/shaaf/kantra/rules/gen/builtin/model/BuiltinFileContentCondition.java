package dev.shaaf.kantra.rules.gen.builtin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;

public record BuiltinFileContentCondition(@JsonProperty("builtin.filecontent") BuiltinFileContent details) implements Condition {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinFileContent(String filePattern, String pattern) {}
}