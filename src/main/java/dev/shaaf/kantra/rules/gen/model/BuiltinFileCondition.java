package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinFileCondition(
    @JsonProperty("builtin.file") BuiltinFileDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinFileCondition(BuiltinFileDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinFileDetails(
        String pattern
    ) {}
}
