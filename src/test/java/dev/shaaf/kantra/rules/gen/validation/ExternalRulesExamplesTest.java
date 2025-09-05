package dev.shaaf.kantra.rules.gen.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalRulesExamplesTest {

    @Test
    void testSpringBootRemovalExamplesValidate() {
        // A couple of representative examples adapted from the referenced ruleset
        String example1 = """
            ruleID: "remove-spring-security-web"
            category: "mandatory"
            when:
              java.dependency:
                name_regex: "org.springframework.security:spring-security-web"
            """;
        String example2 = """
            ruleID: "detect-deprecated-websecurityconfigureradapter"
            category: "mandatory"
            when:
              java.referenced:
                pattern: "org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter"
                location: "CLASS"
            """;
        String example3 = """
            ruleID: "xml-security-namespace"
            category: "optional"
            when:
              builtin.xml:
                xpath: "//beans:beans"
                namespace:
                  beans: "http://www.springframework.org/schema/beans"
            """;

        RuleValidator validator = new RuleValidator();
        assertTrue(validator.validateYamlRule(example1).isValid());
        assertTrue(validator.validateYamlRule(example2).isValid());
        assertTrue(validator.validateYamlRule(example3).isValid());
    }
}
