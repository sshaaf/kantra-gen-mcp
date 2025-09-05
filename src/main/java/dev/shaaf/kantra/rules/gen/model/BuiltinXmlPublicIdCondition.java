package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuiltinXmlPublicIdCondition(
    List<String> filepaths,
    Map<String, String> namespaces,
    String regex,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public BuiltinXmlPublicIdCondition(String regex) {
        this(null, null, regex, null, null, null, null);
    }
    
    public BuiltinXmlPublicIdCondition(List<String> filepaths, Map<String, String> namespaces, String regex) {
        this(filepaths, namespaces, regex, null, null, null, null);
    }
}
