package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Annotated(String pattern, List<Element> elements) {}