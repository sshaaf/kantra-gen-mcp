package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomVariable(
    String name,
    @JsonProperty("defaultValue") String defaultValue,
    @JsonProperty("nameOfCaptureGroup") String nameOfCaptureGroup
) {}