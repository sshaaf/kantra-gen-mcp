package dev.shaaf.kantra.rules.gen.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    POTENTIAL("potential"), 
    OPTIONAL("optional"), 
    MANDATORY("mandatory");
    
    private final String value;
    
    Category(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static Category fromValue(String value) {
        // Handle both uppercase enum names and lowercase values
        for (Category category : Category.values()) {
            if (category.name().equals(value) || category.value.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}