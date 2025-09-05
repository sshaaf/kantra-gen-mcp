package dev.shaaf.kantra.rules.gen.validation;

import dev.shaaf.kantra.rules.gen.model.*;
import org.junit.jupiter.api.Test;

public class SimpleSerializationTest {

    @Test
    void testJavaReferencedConditionSerialization() throws Exception {
        RuleValidator validator = new RuleValidator();
        
        // Create a simple Java referenced condition
        JavaReferencedCondition.JavaReferencedDetails details = 
            new JavaReferencedCondition.JavaReferencedDetails(
                "org.apache.camel.ThreadPoolRejectedPolicy",
                "IMPORT",
                null,
                null
            );
        
        JavaReferencedCondition condition = new JavaReferencedCondition(details);
        
        Rule rule = new Rule(
            "test-rule",
            "Test message",
            "Test description", 
            Category.MANDATORY,
            1,
            null,
            null,
            null,
            condition
        );
        
        String yaml = validator.ruleToYaml(rule);
        System.out.println("Generated YAML:");
        System.out.println(yaml);
        
        // Try to parse it back
        try {
            Rule parsedRule = validator.parseYamlToRule(yaml);
            System.out.println("Successfully parsed back!");
        } catch (Exception e) {
            System.out.println("Failed to parse: " + e.getMessage());
        }
    }
}
