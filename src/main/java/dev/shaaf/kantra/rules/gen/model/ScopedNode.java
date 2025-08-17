package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record ScopedNode(ScopeKind scope, String name, @JsonUnwrapped WhenNode inner) implements WhenNode {}
