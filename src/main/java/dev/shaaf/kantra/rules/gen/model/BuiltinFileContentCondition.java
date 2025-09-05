package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinFileContentCondition(
    String filePattern,
    String pattern,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinFileContentCondition(String filePattern, String pattern) {
        this(filePattern, pattern, null, null, null, null);
    }
}
