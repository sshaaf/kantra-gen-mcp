package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinXmlCondition(
    List<String> filepaths,
    @JsonProperty("namespace") Map<String, String> namespaces,
    String xpath,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinXmlCondition(String xpath) {
        this(null, null, xpath, null, null, null, null);
    }
    
    public BuiltinXmlCondition(List<String> filepaths, Map<String, String> namespaces, String xpath) {
        this(filepaths, namespaces, xpath, null, null, null, null);
    }
}
