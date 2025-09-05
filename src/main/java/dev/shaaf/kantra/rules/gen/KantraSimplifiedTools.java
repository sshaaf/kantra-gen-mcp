package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.*;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Simplified, consolidated tools for Kantra rule generation.
 * This replaces the complex multi-tool approach with clear, single-purpose tools.
 */
@ApplicationScoped
public class KantraSimplifiedTools {

    @Inject
    RuleValidator ruleValidator;

    // ========== PRIMARY RULE BUILDERS ==========

    @Tool(description = "Create a Kantra rule to detect Java import statements (e.g., javax.* to jakarta.* migrations)")
    public String createJavaImportRule(
            @ToolArg(description = "Rule ID (e.g., 'javax-to-jakarta-persistence')") String ruleID,
            @ToolArg(description = "What Java package/class to detect (e.g., 'javax.persistence.*')") String javaPattern,
            @ToolArg(description = "Migration message with before/after examples (e.g., 'Change javax.persistence to jakarta.persistence. Before: import javax.persistence.Entity; After: import jakarta.persistence.Entity;')") String message,
            @ToolArg(description = "Rule urgency: MANDATORY (must fix), OPTIONAL (should fix), POTENTIAL (might fix)") Category category,
            @ToolArg(description = "Effort level 1-5 (1=trivial, 5=major refactoring)") Integer effort) {

        JavaReferencedCondition condition = new JavaReferencedCondition(javaPattern, JavaLocation.IMPORT.toString());
        Rule rule = new Rule(
            ruleID, message, "Detects Java import: " + javaPattern, category, effort,
            List.of("java", "import", "migration"), List.of(), List.of(), condition
        );
        try {
            return ruleValidator.ruleToYaml(rule);
        } catch (Exception e) {
            return "Error: Could not serialize rule to YAML. " + e.getMessage();
        }
    }

    @Tool(description = "Create a Kantra rule to detect Java class usage (e.g., deprecated classes, method calls)")
    public String createJavaClassRule(
            @ToolArg(description = "Rule ID (e.g., 'deprecated-thread-pool-policy')") String ruleID,
            @ToolArg(description = "What Java class/method to detect (e.g., 'org.apache.camel.ThreadPoolRejectedPolicy')") String javaPattern,
            @ToolArg(description = "Where to look: IMPORT, CLASS, METHOD_CALL, CONSTRUCTOR_CALL, ANNOTATION, FIELD, METHOD") JavaLocation location,
            @ToolArg(description = "Migration message with before/after examples") String message,
            @ToolArg(description = "Rule urgency: MANDATORY, OPTIONAL, POTENTIAL") Category category,
            @ToolArg(description = "Effort level 1-5") Integer effort) {

        JavaReferencedCondition condition = new JavaReferencedCondition(javaPattern, location.toString());
        Rule rule = new Rule(
            ruleID, message, "Detects Java " + location.toString().toLowerCase() + ": " + javaPattern, 
            category, effort, List.of("java", location.toString().toLowerCase()), List.of(), List.of(), condition
        );
        try {
            return ruleValidator.ruleToYaml(rule);
        } catch (Exception e) {
            return "Error: Could not serialize rule to YAML. " + e.getMessage();
        }
    }

    @Tool(description = "Create a Kantra rule to detect content inside files (e.g., System.out.println, specific text patterns)")
    public String createFileContentRule(
            @ToolArg(description = "Rule ID (e.g., 'detect-system-out-println')") String ruleID,
            @ToolArg(description = "File pattern to search (e.g., '*.java' for Java files, '*.xml' for XML files)") String filePattern,
            @ToolArg(description = "Text/regex pattern to find (e.g., 'System\\.out\\.println' or 'Hello.*World')") String contentPattern,
            @ToolArg(description = "Migration message with before/after examples") String message,
            @ToolArg(description = "Rule urgency: MANDATORY, OPTIONAL, POTENTIAL") Category category,
            @ToolArg(description = "Effort level 1-5") Integer effort) {

        BuiltinFileContentCondition condition = new BuiltinFileContentCondition(filePattern, contentPattern);
        Rule rule = new Rule(
            ruleID, message, "Detects content in " + filePattern + ": " + contentPattern,
            category, effort, List.of("file-content", "pattern"), List.of(), List.of(), condition
        );
        try {
            return ruleValidator.ruleToYaml(rule);
        } catch (Exception e) {
            return "Error: Could not serialize rule to YAML. " + e.getMessage();
        }
    }

    @Tool(description = "Create a Kantra rule to detect XML content using XPath expressions")
    public String createXmlRule(
            @ToolArg(description = "Rule ID (e.g., 'deprecated-xml-config')") String ruleID,
            @ToolArg(description = "XPath expression to find XML elements (e.g., '//bean[@class=\"com.example.OldClass\"]')") String xpath,
            @ToolArg(description = "Migration message with before/after examples") String message,
            @ToolArg(description = "Rule urgency: MANDATORY, OPTIONAL, POTENTIAL") Category category,
            @ToolArg(description = "Effort level 1-5") Integer effort) {

        BuiltinXmlCondition condition = new BuiltinXmlCondition(null, null, xpath);
        Rule rule = new Rule(
            ruleID, message, "Detects XML content: " + xpath,
            category, effort, List.of("xml", "configuration"), List.of(), List.of(), condition
        );
        try {
            return ruleValidator.ruleToYaml(rule);
        } catch (Exception e) {
            return "Error: Could not serialize rule to YAML. " + e.getMessage();
        }
    }

    // ========== CONDITION COMBINERS ==========

    @Tool(description = "Combine multiple conditions with AND logic (all conditions must match)")
    public Condition combineWithAnd(@ToolArg(description = "List of conditions to combine") List<Condition> conditions) {
        return AndCondition.fromArray(conditions);
    }

    @Tool(description = "Combine multiple conditions with OR logic (any condition can match)")
    public Condition combineWithOr(@ToolArg(description = "List of conditions to combine") List<Condition> conditions) {
        return OrCondition.fromArray(conditions);
    }

    // ========== UTILITIES ==========

    @Tool(description = "Validate a YAML rule file for syntax and schema compliance")
    public String validateRule(@ToolArg(description = "YAML content to validate") String yamlContent) {
        try {
            RuleValidator.ValidationResult result = ruleValidator.validateYamlRule(yamlContent);
            if (result.isValid()) {
                return "✅ Rule is valid!";
            } else {
                return "❌ Rule validation failed: " + result.errors();
            }
        } catch (Exception e) {
            return "❌ Validation error: " + e.getMessage();
        }
    }

    @Tool(description = "Get help and examples for creating Kantra rules")
    public String getHelp(@ToolArg(description = "What you want to create (e.g., 'java import rule', 'file content rule', 'xml rule')") String request) {
        if (request.toLowerCase().contains("import")) {
            return """
                ## Java Import Rule Example
                ```
                createJavaImportRule(
                    ruleID: "javax-to-jakarta-persistence",
                    javaPattern: "javax.persistence.*",
                    message: "Migrate from javax.persistence to jakarta.persistence. Before: import javax.persistence.Entity; After: import jakarta.persistence.Entity;",
                    category: MANDATORY,
                    effort: 1
                )
                ```
                """;
        } else if (request.toLowerCase().contains("content") || request.toLowerCase().contains("println")) {
            return """
                ## File Content Rule Example
                ```
                createFileContentRule(
                    ruleID: "detect-system-out",
                    filePattern: "*.java",
                    contentPattern: "System\\\\.out\\\\.println",
                    message: "Replace System.out.println with proper logging. Before: System.out.println(\"Hello\"); After: logger.info(\"Hello\");",
                    category: POTENTIAL,
                    effort: 1
                )
                ```
                """;
        } else if (request.toLowerCase().contains("class") || request.toLowerCase().contains("method")) {
            return """
                ## Java Class/Method Rule Example
                ```
                createJavaClassRule(
                    ruleID: "deprecated-thread-pool",
                    javaPattern: "org.apache.camel.ThreadPoolRejectedPolicy",
                    location: IMPORT,
                    message: "ThreadPoolRejectedPolicy moved. Before: import org.apache.camel.ThreadPoolRejectedPolicy; After: import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;",
                    category: MANDATORY,
                    effort: 2
                )
                ```
                """;
        } else {
            return """
                ## Available Rule Types
                
                1. **Java Import Rules**: `createJavaImportRule()` - For package migrations
                2. **Java Class Rules**: `createJavaClassRule()` - For class/method usage
                3. **File Content Rules**: `createFileContentRule()` - For text patterns in files
                4. **XML Rules**: `createXmlRule()` - For XML configuration changes
                
                Use `getHelp("import")`, `getHelp("content")`, etc. for specific examples.
                """;
        }
    }
}
