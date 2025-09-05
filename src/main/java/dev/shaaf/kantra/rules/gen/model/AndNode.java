package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AndNode(@JsonProperty("and") List<WhenNode> allOf) implements WhenNode {}
