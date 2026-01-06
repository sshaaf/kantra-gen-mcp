package dev.shaaf.kantra.rules.gen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.kantra.rules.gen.commands.java.CreateJavaRuleCommand;
import dev.shaaf.kantra.rules.gen.commands.file.CreateFileContentRuleCommand;
import dev.shaaf.kantra.rules.gen.commands.file.CreateFileRuleCommand;
import dev.shaaf.kantra.rules.gen.commands.xml.CreateXmlRuleCommand;
import dev.shaaf.kantra.rules.gen.commands.json.CreateJsonRuleCommand;
import dev.shaaf.kantra.rules.gen.commands.util.ValidateRuleCommand;
import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.JavaLocation;
import dev.shaaf.kantra.rules.gen.model.JavaReferencedCondition;
import dev.shaaf.kantra.rules.gen.model.Rule;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Kantra Tool commands using the parametric collapse pattern.
 * These tests directly test the command classes, bypassing CDI container.
 */
public class KantraToolTest {

    private ObjectMapper mapper;
    private RuleValidator validator;
    
    // Commands under test
    private CreateJavaRuleCommand javaRuleCommand;
    private CreateFileContentRuleCommand fileContentRuleCommand;
    private CreateFileRuleCommand fileRuleCommand;
    private CreateXmlRuleCommand xmlRuleCommand;
    private CreateJsonRuleCommand jsonRuleCommand;
    private ValidateRuleCommand validateRuleCommand;

    @BeforeEach
    void setup() throws Exception {
        mapper = new ObjectMapper();
        validator = new RuleValidator();
        
        // Initialize commands
        javaRuleCommand = new CreateJavaRuleCommand();
        fileContentRuleCommand = new CreateFileContentRuleCommand();
        fileRuleCommand = new CreateFileRuleCommand();
        xmlRuleCommand = new CreateXmlRuleCommand();
        jsonRuleCommand = new CreateJsonRuleCommand();
        validateRuleCommand = new ValidateRuleCommand();
        
        // Inject dependencies manually
        injectDependencies(javaRuleCommand);
        injectDependencies(fileContentRuleCommand);
        injectDependencies(fileRuleCommand);
        injectDependencies(xmlRuleCommand);
        injectDependencies(jsonRuleCommand);
        injectDependencies(validateRuleCommand);
    }
    
    private void injectDependencies(Object command) throws Exception {
        // Inject mapper
        Field mapperField = command.getClass().getSuperclass().getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(command, mapper);
        
        // Inject ruleValidator
        Field validatorField = command.getClass().getSuperclass().getDeclaredField("ruleValidator");
        validatorField.setAccessible(true);
        validatorField.set(command, validator);
    }

    // ========== JAVA RULE COMMAND TESTS ==========

    @Test
    void testCreateJavaClassRule() throws Exception {
        String params = """
            {
                "ruleID": "deprecated-thread-pool",
                "javaPattern": "org.apache.camel.ThreadPoolRejectedPolicy",
                "location": "IMPORT",
                "message": "Use new ThreadPoolRejectedPolicy",
                "category": "MANDATORY",
                "effort": 2
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = javaRuleCommand.execute(paramsNode);
        
        assertNotNull(yaml);
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
        
        Rule rule = validator.parseYamlToRule(yaml);
        assertEquals("deprecated-thread-pool", rule.ruleId());
        assertEquals(Category.MANDATORY, rule.category());
        assertNotNull(rule.when());
    }

    @Test
    void testCreateFileContentRule() throws Exception {
        String params = """
            {
                "ruleID": "detect-system-out",
                "filePattern": "*.java",
                "contentPattern": "System\\\\.out\\\\.println",
                "message": "Replace with logger",
                "category": "POTENTIAL",
                "effort": 1
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = fileContentRuleCommand.execute(paramsNode);
        
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
    }

    @Test
    void testCreateFileRule() throws Exception {
        String params = """
            {
                "ruleID": "detect-old-config",
                "filePattern": "applicationContext.xml",
                "message": "Migrate XML config to Java",
                "category": "MANDATORY",
                "effort": 3
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = fileRuleCommand.execute(paramsNode);
        
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
    }

    @Test
    void testCreateXmlRule() throws Exception {
        String params = """
            {
                "ruleID": "xml-dependency",
                "xpath": "//dependency[artifactId='camel-core']",
                "message": "Update dependency",
                "category": "OPTIONAL",
                "effort": 1
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = xmlRuleCommand.execute(paramsNode);
        
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
    }

    @Test
    void testCreateJsonRule() throws Exception {
        String params = """
            {
                "ruleID": "json-config",
                "xpath": "$.config.deprecated",
                "message": "Remove deprecated config",
                "category": "OPTIONAL",
                "effort": 1
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = jsonRuleCommand.execute(paramsNode);
        
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid: " + result.errors());
    }

    @Test
    void testValidateRuleCommand() throws Exception {
        String validYaml = "ruleID: test\ncategory: mandatory\nwhen:\n  builtin.file:\n    pattern: '*.java'\n";
        String params = String.format("{\"yamlContent\": \"%s\"}", 
            validYaml.replace("\n", "\\n").replace("\"", "\\\""));
        
        JsonNode paramsNode = mapper.readTree(params);
        String response = validateRuleCommand.execute(paramsNode);
        assertEquals("Rule is valid!", response);
    }

    // ========== COMPREHENSIVE JAVA LOCATION TESTS ==========

    @ParameterizedTest
    @EnumSource(JavaLocation.class)
    void testCreateJavaRuleWithAllLocations(JavaLocation location) throws Exception {
        String ruleID = "test-" + location.name().toLowerCase().replace("_", "-");
        String javaPattern = getTestPatternForLocation(location);
        String message = getTestMessageForLocation(location);
        
        String params = String.format("""
            {
                "ruleID": "%s",
                "javaPattern": "%s",
                "location": "%s",
                "message": "%s",
                "category": "MANDATORY",
                "effort": 2
            }
            """, ruleID, javaPattern, location.name(), escapeJson(message));
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = javaRuleCommand.execute(paramsNode);
        
        assertNotNull(yaml, "YAML should not be null for location: " + location);
        
        var result = validator.validateYamlRule(yaml);
        assertTrue(result.isValid(), "Generated YAML should be valid for location " + location + ". Errors: " + result.errors());
        
        Rule rule = validator.parseYamlToRule(yaml);
        assertEquals(ruleID, rule.ruleId(), "Rule ID should match for location: " + location);
        assertEquals(Category.MANDATORY, rule.category(), "Category should match for location: " + location);
        assertNotNull(rule.when(), "When condition should not be null for location: " + location);
        
        // Verify the condition is a JavaReferencedCondition with correct location
        assertTrue(rule.when() instanceof JavaReferencedCondition, 
            "Condition should be JavaReferencedCondition for location: " + location);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals(javaPattern, condition.pattern(), "Pattern should match for location: " + location);
        assertEquals(location.toString(), condition.location(), "Location should match for location: " + location);
    }

    @Test
    void testJavaLocationInheritanceRule() throws Exception {
        String params = """
            {
                "ruleID": "spring-controller-inheritance",
                "javaPattern": "org.springframework.web.servlet.mvc.AbstractController",
                "location": "INHERITANCE",
                "message": "Replace AbstractController inheritance. Before: class MyController extends AbstractController { ... } After: class MyController { @Autowired private SomeService service; ... }",
                "category": "MANDATORY",
                "effort": 3
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = javaRuleCommand.execute(paramsNode);
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("INHERITANCE", condition.location());
        assertTrue(rule.message().contains("Before:") && rule.message().contains("After:"));
    }

    @Test
    void testJavaLocationMethodCallRule() throws Exception {
        String params = """
            {
                "ruleID": "servlet-request-method",
                "javaPattern": "javax.servlet.http.HttpServletRequest.getParameter",
                "location": "METHOD_CALL",
                "message": "Update servlet method calls",
                "category": "OPTIONAL",
                "effort": 2
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = javaRuleCommand.execute(paramsNode);
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("METHOD_CALL", condition.location());
        assertTrue(condition.pattern().contains("getParameter"));
    }

    @Test
    void testJavaLocationConstructorCallRule() throws Exception {
        String params = """
            {
                "ruleID": "vector-constructor",
                "javaPattern": "java.util.Vector",
                "location": "CONSTRUCTOR_CALL",
                "message": "Replace Vector with ArrayList",
                "category": "POTENTIAL",
                "effort": 1
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = javaRuleCommand.execute(paramsNode);
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("CONSTRUCTOR_CALL", condition.location());
        assertEquals("java.util.Vector", condition.pattern());
    }

    @Test
    void testJavaLocationAnnotationRule() throws Exception {
        String params = """
            {
                "ruleID": "ejb-stateless-annotation",
                "javaPattern": "javax.ejb.Stateless",
                "location": "ANNOTATION",
                "message": "Migrate EJB annotations",
                "category": "MANDATORY",
                "effort": 2
            }
            """;
        
        JsonNode paramsNode = mapper.readTree(params);
        String yaml = javaRuleCommand.execute(paramsNode);
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("ANNOTATION", condition.location());
        assertTrue(condition.pattern().contains("javax.ejb.Stateless"));
    }

    @Test
    void testOperationEnum() {
        // Verify all expected operations exist
        assertNotNull(KantraOperation.CREATE_JAVA_CLASS_RULE);
        assertNotNull(KantraOperation.CREATE_FILE_CONTENT_RULE);
        assertNotNull(KantraOperation.CREATE_FILE_RULE);
        assertNotNull(KantraOperation.CREATE_XML_RULE);
        assertNotNull(KantraOperation.CREATE_JSON_RULE);
        assertNotNull(KantraOperation.VALIDATE_RULE);
        assertNotNull(KantraOperation.GET_HELP);
    }

    @Test
    void testCommandOperationMapping() {
        assertEquals(KantraOperation.CREATE_JAVA_CLASS_RULE, javaRuleCommand.getOperation());
        assertEquals(KantraOperation.CREATE_FILE_CONTENT_RULE, fileContentRuleCommand.getOperation());
        assertEquals(KantraOperation.CREATE_FILE_RULE, fileRuleCommand.getOperation());
        assertEquals(KantraOperation.CREATE_XML_RULE, xmlRuleCommand.getOperation());
        assertEquals(KantraOperation.CREATE_JSON_RULE, jsonRuleCommand.getOperation());
        assertEquals(KantraOperation.VALIDATE_RULE, validateRuleCommand.getOperation());
    }

    @Test
    void testCommandRequiredParams() {
        assertArrayEquals(
            new String[]{"ruleID", "javaPattern", "location", "message", "category", "effort"},
            javaRuleCommand.getRequiredParams()
        );
        assertArrayEquals(
            new String[]{"ruleID", "filePattern", "contentPattern", "message", "category", "effort"},
            fileContentRuleCommand.getRequiredParams()
        );
        assertArrayEquals(
            new String[]{"ruleID", "xpath", "message", "category", "effort"},
            xmlRuleCommand.getRequiredParams()
        );
    }

    // ========== Helper Methods ==========

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String getTestPatternForLocation(JavaLocation location) {
        return switch (location) {
            case INHERITANCE -> "org.springframework.web.servlet.mvc.AbstractController";
            case METHOD_CALL -> "javax.servlet.http.HttpServletRequest.getParameter";
            case CONSTRUCTOR_CALL -> "java.util.Vector";
            case ANNOTATION -> "javax.ejb.Stateless";
            case IMPLEMENTS_TYPE -> "javax.servlet.Filter";
            case ENUM -> "javax.persistence.CascadeType";
            case RETURN_TYPE -> "javax.servlet.http.HttpSession";
            case IMPORT -> "org.apache.camel.ThreadPoolRejectedPolicy";
            case VARIABLE_DECLARATION -> "java.util.Hashtable";
            case TYPE -> "javax.xml.bind.JAXBContext";
            case PACKAGE -> "javax.servlet";
            case FIELD -> "javax.servlet.http.HttpServletResponse.SC_OK";
            case METHOD -> "javax.persistence.EntityManager.persist";
            case CLASS -> "javax.servlet.ServletException";
        };
    }

    private String getTestMessageForLocation(JavaLocation location) {
        return switch (location) {
            case INHERITANCE -> "Replace AbstractController inheritance";
            case METHOD_CALL -> "Update servlet method calls";
            case CONSTRUCTOR_CALL -> "Replace Vector with ArrayList";
            case ANNOTATION -> "Migrate EJB annotations";
            case IMPLEMENTS_TYPE -> "Update Filter interface";
            case ENUM -> "Update JPA enum usage";
            case RETURN_TYPE -> "Update return type";
            case IMPORT -> "Update import statement";
            case VARIABLE_DECLARATION -> "Replace Hashtable with HashMap";
            case TYPE -> "Update JAXB type usage";
            case PACKAGE -> "Migrate servlet package";
            case FIELD -> "Update status code field";
            case METHOD -> "Update JPA method";
            case CLASS -> "Update exception class";
        };
    }
}

