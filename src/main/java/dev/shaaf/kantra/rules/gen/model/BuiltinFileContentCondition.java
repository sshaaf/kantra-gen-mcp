package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinFileContentCondition(
    @JsonProperty("builtin.filecontent") BuiltinFileContentDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinFileContentCondition(BuiltinFileContentDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BuiltinFileContentDetails(
        String filePattern,
        String pattern
    ) {}
}
