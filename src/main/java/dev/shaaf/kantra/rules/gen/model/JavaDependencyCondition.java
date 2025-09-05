package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JavaDependencyCondition(
    @JsonProperty("java.dependency") JavaDependencyDetails details,
    String as,
    String from,
    Boolean ignore,
    Boolean not
) implements Condition {
    
    public JavaDependencyCondition(JavaDependencyDetails details) {
        this(details, null, null, null, null);
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JavaDependencyDetails(
        String name,
        @JsonProperty("name_regex") String nameRegex,
        String upperbound,
        String lowerbound
    ) {}
}
