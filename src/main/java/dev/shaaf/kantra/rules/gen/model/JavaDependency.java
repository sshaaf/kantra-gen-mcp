package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record JavaDependency(
        String name,
        @JsonProperty("name_regex") String nameRegex,
        String upperbound,
        String lowerbound
) implements WhenNode {}
