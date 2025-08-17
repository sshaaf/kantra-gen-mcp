package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomVariable(
        String name,
        String defaultValue,
        String nameOfCaptureGroup
) {}