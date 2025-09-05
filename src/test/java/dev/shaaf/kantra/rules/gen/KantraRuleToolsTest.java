package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.JavaLocation;
import dev.shaaf.kantra.rules.gen.model.Rule;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KantraRuleToolsTest {

    private KantraRuleTools tools;
    private RuleValidator validator;

    @BeforeEach
    void setup() {
        validator = new RuleValidator();
        tools = new KantraRuleTools();
        // Inject manually because we are not running CDI container in unit tests
        try {
            var field = KantraRuleTools.class.getDeclaredField("ruleValidator");
            field.setAccessible(true);
            field.set(tools, validator);
        } catch (Exception e) {
            fail("Failed to inject RuleValidator: " + e.getMessage());
        }
    }

    @Test
    void testCreateJavaClassRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "deprecated-thread-pool",
                "org.apache.camel.ThreadPoolRejectedPolicy",
                JavaLocation.IMPORT,
                "Use new ThreadPoolRejectedPolicy",
                Category.MANDATORY,
                2
        );
        assertNotNull(yaml);
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
        Rule rule = validator.parseYamlToRule(yaml);
        assertEquals("deprecated-thread-pool", rule.ruleId());
        assertEquals(Category.MANDATORY, rule.category());
        assertNotNull(rule.when());
    }

    @Test
    void testCreateFileContentRule() {
        String yaml = tools.createFileContentRule(
                "detect-system-out",
                "*.java",
                "System\\.out\\.println",
                "Replace with logger",
                Category.POTENTIAL,
                1
        );
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
    }

    @Test
    void testCreateXmlRule() {
        String yaml = tools.createXmlRule(
                "xml-dependency",
                "//dependency[artifactId='camel-core']",
                "Update dependency",
                Category.OPTIONAL,
                1
        );
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
    }

    @Test
    void testValidateRuleUtility() {
        String validYaml = "ruleID: test\ncategory: mandatory\nwhen:\n  builtin.file:\n    pattern: '*.java'\n";
        String response = tools.validateRule(validYaml);
        assertEquals("Rule is valid!", response);

        String invalidYaml = "ruleID: test\ncategory: invalid\nwhen: {}\n";
        String responseInvalid = tools.validateRule(invalidYaml);
        assertTrue(responseInvalid.startsWith("Rule validation failed:"));
    }

    @Test
    void testGetHelpVariants() {
        String helpImport = tools.getHelp("import");
        assertTrue(helpImport.contains("Java Import Rule Example"));

        String helpContent = tools.getHelp("content");
        assertTrue(helpContent.contains("File Content Rule Example"));

        String helpClass = tools.getHelp("class");
        assertTrue(helpClass.contains("Java Class/Method Rule Example"));

        String helpDefault = tools.getHelp("something else");
        assertTrue(helpDefault.contains("Available Rule Types"));
    }
}
