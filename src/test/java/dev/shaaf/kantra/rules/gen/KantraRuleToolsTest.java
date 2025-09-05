package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.JavaLocation;
import dev.shaaf.kantra.rules.gen.model.JavaReferencedCondition;
import dev.shaaf.kantra.rules.gen.model.Rule;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    // ========== COMPREHENSIVE JAVA LOCATION TESTS ==========

    @ParameterizedTest
    @EnumSource(JavaLocation.class)
    void testCreateJavaClassRuleWithAllLocations(JavaLocation location) throws Exception {
        String ruleID = "test-" + location.name().toLowerCase().replace("_", "-");
        String javaPattern = getTestPatternForLocation(location);
        String message = getTestMessageForLocation(location);
        
        String yaml = tools.createJavaClassRule(
                ruleID,
                javaPattern,
                location,
                message,
                Category.MANDATORY,
                2
        );
        
        assertNotNull(yaml, "YAML should not be null for location: " + location);
        assertFalse(yaml.startsWith("Error:"), "Should not contain error for location: " + location + ". YAML: " + yaml);
        
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
        String yaml = tools.createJavaClassRule(
                "spring-controller-inheritance",
                "org.springframework.web.servlet.mvc.AbstractController",
                JavaLocation.INHERITANCE,
                "Replace AbstractController inheritance. Before: class MyController extends AbstractController { ... } After: class MyController { @Autowired private SomeService service; ... }",
                Category.MANDATORY,
                3
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("INHERITANCE", condition.location());
        assertTrue(rule.message().contains("Before:") && rule.message().contains("After:"));
    }

    @Test
    void testJavaLocationMethodCallRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "servlet-request-method",
                "javax.servlet.http.HttpServletRequest.getParameter",
                JavaLocation.METHOD_CALL,
                "Update servlet method calls. Before: String param = request.getParameter(\"name\"); After: String param = request.getParameter(\"name\"); // Add validation",
                Category.OPTIONAL,
                2
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("METHOD_CALL", condition.location());
        assertTrue(condition.pattern().contains("getParameter"));
    }

    @Test
    void testJavaLocationConstructorCallRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "vector-constructor",
                "java.util.Vector",
                JavaLocation.CONSTRUCTOR_CALL,
                "Replace Vector with ArrayList. Before: List<String> list = new Vector<>(); After: List<String> list = new ArrayList<>();",
                Category.POTENTIAL,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("CONSTRUCTOR_CALL", condition.location());
        assertEquals("java.util.Vector", condition.pattern());
    }

    @Test
    void testJavaLocationAnnotationRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "ejb-stateless-annotation",
                "javax.ejb.Stateless",
                JavaLocation.ANNOTATION,
                "Migrate EJB annotations. Before: @Stateless public class MyBean { ... } After: @Component public class MyBean { ... }",
                Category.MANDATORY,
                2
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("ANNOTATION", condition.location());
        assertTrue(condition.pattern().contains("javax.ejb.Stateless"));
    }

    @Test
    void testJavaLocationImplementsTypeRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "servlet-filter-interface",
                "javax.servlet.Filter",
                JavaLocation.IMPLEMENTS_TYPE,
                "Update Filter interface. Before: public class MyFilter implements Filter { ... } After: public class MyFilter implements jakarta.servlet.Filter { ... }",
                Category.MANDATORY,
                2
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("IMPLEMENTS_TYPE", condition.location());
        assertEquals("javax.servlet.Filter", condition.pattern());
    }

    @Test
    void testJavaLocationEnumRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "persistence-cascade-enum",
                "javax.persistence.CascadeType",
                JavaLocation.ENUM,
                "Update JPA enum usage. Before: @OneToMany(cascade = CascadeType.ALL) After: @OneToMany(cascade = jakarta.persistence.CascadeType.ALL)",
                Category.MANDATORY,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("ENUM", condition.location());
        assertTrue(condition.pattern().contains("CascadeType"));
    }

    @Test
    void testJavaLocationReturnTypeRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "http-session-return",
                "javax.servlet.http.HttpSession",
                JavaLocation.RETURN_TYPE,
                "Update return type. Before: HttpSession getSession() { ... } After: jakarta.servlet.http.HttpSession getSession() { ... }",
                Category.MANDATORY,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("RETURN_TYPE", condition.location());
        assertTrue(condition.pattern().contains("HttpSession"));
    }

    @Test
    void testJavaLocationImportRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "camel-import-migration",
                "org.apache.camel.ThreadPoolRejectedPolicy",
                JavaLocation.IMPORT,
                "Update import statement. Before: import org.apache.camel.ThreadPoolRejectedPolicy; After: import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;",
                Category.MANDATORY,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("IMPORT", condition.location());
        assertEquals("org.apache.camel.ThreadPoolRejectedPolicy", condition.pattern());
        assertEquals(Category.MANDATORY, rule.category());
    }

    @Test
    void testJavaLocationVariableDeclarationRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "hashtable-variable",
                "java.util.Hashtable",
                JavaLocation.VARIABLE_DECLARATION,
                "Replace Hashtable with HashMap. Before: Hashtable<String, String> map = new Hashtable<>(); After: Map<String, String> map = new HashMap<>();",
                Category.POTENTIAL,
                2
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("VARIABLE_DECLARATION", condition.location());
        assertEquals("java.util.Hashtable", condition.pattern());
    }

    @Test
    void testJavaLocationTypeRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "jaxb-context-type",
                "javax.xml.bind.JAXBContext",
                JavaLocation.TYPE,
                "Update JAXB type usage. Before: JAXBContext context = ...; After: jakarta.xml.bind.JAXBContext context = ...;",
                Category.MANDATORY,
                2
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("TYPE", condition.location());
        assertTrue(condition.pattern().contains("JAXBContext"));
    }

    @Test
    void testJavaLocationPackageRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "servlet-package-migration",
                "javax.servlet.*",
                JavaLocation.PACKAGE,
                "Migrate servlet package. Before: import javax.servlet.*; After: import jakarta.servlet.*;",
                Category.MANDATORY,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("PACKAGE", condition.location());
        assertTrue(condition.pattern().contains("javax.servlet.*"));
    }

    @Test
    void testJavaLocationFieldRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "http-status-field",
                "javax.servlet.http.HttpServletResponse.SC_OK",
                JavaLocation.FIELD,
                "Update status code field. Before: int status = HttpServletResponse.SC_OK; After: int status = jakarta.servlet.http.HttpServletResponse.SC_OK;",
                Category.MANDATORY,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("FIELD", condition.location());
        assertTrue(condition.pattern().contains("SC_OK"));
    }

    @Test
    void testJavaLocationMethodRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "entity-manager-method",
                "javax.persistence.EntityManager.persist",
                JavaLocation.METHOD,
                "Update JPA method. Before: entityManager.persist(entity); After: // Add proper transaction handling",
                Category.OPTIONAL,
                2
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("METHOD", condition.location());
        assertTrue(condition.pattern().contains("persist"));
    }

    @Test
    void testJavaLocationClassRule() throws Exception {
        String yaml = tools.createJavaClassRule(
                "servlet-exception-class",
                "javax.servlet.ServletException",
                JavaLocation.CLASS,
                "Update exception class. Before: throw new ServletException(\"error\"); After: throw new jakarta.servlet.ServletException(\"error\");",
                Category.MANDATORY,
                1
        );
        
        Rule rule = validator.parseYamlToRule(yaml);
        JavaReferencedCondition condition = (JavaReferencedCondition) rule.when();
        assertEquals("CLASS", condition.location());
        assertEquals("javax.servlet.ServletException", condition.pattern());
    }

    // Helper methods for parameterized tests
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
            case PACKAGE -> "javax.servlet.*";
            case FIELD -> "javax.servlet.http.HttpServletResponse.SC_OK";
            case METHOD -> "javax.persistence.EntityManager.persist";
            case CLASS -> "javax.servlet.ServletException";
        };
    }

    private String getTestMessageForLocation(JavaLocation location) {
        return switch (location) {
            case INHERITANCE -> "Replace AbstractController inheritance. Before: class MyController extends AbstractController { ... } After: class MyController { @Autowired private SomeService service; ... }";
            case METHOD_CALL -> "Update servlet method calls. Before: String param = request.getParameter(\"name\"); After: String param = request.getParameter(\"name\"); // Add validation";
            case CONSTRUCTOR_CALL -> "Replace Vector with ArrayList. Before: List<String> list = new Vector<>(); After: List<String> list = new ArrayList<>();";
            case ANNOTATION -> "Migrate EJB annotations. Before: @Stateless public class MyBean { ... } After: @Component public class MyBean { ... }";
            case IMPLEMENTS_TYPE -> "Update Filter interface. Before: public class MyFilter implements Filter { ... } After: public class MyFilter implements jakarta.servlet.Filter { ... }";
            case ENUM -> "Update JPA enum usage. Before: @OneToMany(cascade = CascadeType.ALL) After: @OneToMany(cascade = jakarta.persistence.CascadeType.ALL)";
            case RETURN_TYPE -> "Update return type. Before: HttpSession getSession() { ... } After: jakarta.servlet.http.HttpSession getSession() { ... }";
            case IMPORT -> "Update import statement. Before: import org.apache.camel.ThreadPoolRejectedPolicy; After: import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;";
            case VARIABLE_DECLARATION -> "Replace Hashtable with HashMap. Before: Hashtable<String, String> map = new Hashtable<>(); After: Map<String, String> map = new HashMap<>();";
            case TYPE -> "Update JAXB type usage. Before: JAXBContext context = ...; After: jakarta.xml.bind.JAXBContext context = ...;";
            case PACKAGE -> "Migrate servlet package. Before: import javax.servlet.*; After: import jakarta.servlet.*;";
            case FIELD -> "Update status code field. Before: int status = HttpServletResponse.SC_OK; After: int status = jakarta.servlet.http.HttpServletResponse.SC_OK;";
            case METHOD -> "Update JPA method. Before: entityManager.persist(entity); After: // Add proper transaction handling";
            case CLASS -> "Update exception class. Before: throw new ServletException(\"error\"); After: throw new jakarta.servlet.ServletException(\"error\");";
        };
    }
}
