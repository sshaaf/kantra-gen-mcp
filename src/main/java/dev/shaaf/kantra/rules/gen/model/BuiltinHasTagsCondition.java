package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinHasTagsCondition(
    List<String> tags,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinHasTagsCondition(List<String> tags) {
        this(tags, null, null, null, null);
    }
}
