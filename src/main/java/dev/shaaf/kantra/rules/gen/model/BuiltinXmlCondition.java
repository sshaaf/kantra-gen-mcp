package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinXmlCondition(
    @JsonProperty("builtin.xml") BuiltinXmlDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinXmlCondition(BuiltinXmlDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinXmlDetails(
        List<String> filepaths,
        @JsonProperty("namespace") Map<String, String> namespaces,
        String xpath
    ) {}
}
