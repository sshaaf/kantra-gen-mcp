package dev.shaaf.kantra.rules.gen.builtin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shaaf.kantra.rules.gen.core.model.Condition;
import java.util.List;
import java.util.Map;

public record BuiltinXmlCondition(@JsonProperty("builtin.xml") BuiltinXml details) implements Condition {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinXml(List<String> filepaths, Map<String, String> namespaces, String xpath) {}
}