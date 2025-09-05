package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinJsonCondition(
    @JsonProperty("builtin.json") BuiltinJsonDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinJsonCondition(BuiltinJsonDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinJsonDetails(
        List<String> filepaths,
        String xpath
    ) {}
}
