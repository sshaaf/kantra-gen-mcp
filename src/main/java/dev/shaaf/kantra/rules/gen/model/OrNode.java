package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrNode(@JsonProperty("or") List<WhenNode> anyOf) implements WhenNode {}