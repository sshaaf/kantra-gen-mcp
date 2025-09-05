package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinJsonCondition(
    List<String> filepaths,
    String xpath,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinJsonCondition(String xpath) {
        this(null, xpath, null, null, null, null);
    }
    
    public BuiltinJsonCondition(List<String> filepaths, String xpath) {
        this(filepaths, xpath, null, null, null, null);
    }
}
