package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinXmlPublicIdCondition(
    @JsonProperty("builtin.xmlPublicID") BuiltinXmlPublicIdDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinXmlPublicIdCondition(BuiltinXmlPublicIdDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinXmlPublicIdDetails(
        List<String> filepaths,
        Map<String, String> namespaces,
        String regex
    ) {}
}
