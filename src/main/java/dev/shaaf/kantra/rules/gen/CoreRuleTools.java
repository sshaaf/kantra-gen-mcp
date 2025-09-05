package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.*;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Core tools for Kantra rule assembly, logical operations, and serialization.
 * 
 * Common Usage Patterns:
 * 1. Build conditions using JavaRuleTools or BuiltinRuleTools
 * 2. Combine conditions using combineWithAnd() or combineWithOr()
 * 3. Assemble rules using buildRule() or buildRuleWithOptionalParams()
 * 4. Generate YAML using serializeRuleToYaml()
 * 
 * Cross-References:
 * - Use JavaRuleTools.buildJavaReferenced() for Java code conditions
 * - Use BuiltinRuleTools.buildFileCondition() for file-based conditions
 * - See KantraRuleToolsFacade for quick builders and examples
 */
@ApplicationScoped
public class CoreRuleTools {

    @Inject
    RuleValidator ruleValidator;

    @Tool(description = "[CORE] Combines multiple conditions with a logical AND.")
    public Condition combineWithAnd(@ToolArg(description = "A list of conditions to combine.") List<Condition> conditions) {
        return new AndCondition(conditions);
    }

    @Tool(description = "[CORE] Combines multiple conditions with a logical OR.")
    public Condition combineWithOr(@ToolArg(description = "A list of conditions to combine.") List<Condition> conditions) {
        return new OrCondition(conditions);
    }

    @Tool(description = "[CORE] Assembles a simple Kantra rule object without links or custom variables.")
    public Rule buildRule(
            @ToolArg(description = "A unique ID for the rule.") String ruleID,
            @ToolArg(description = "A short description of the rule.") String description,
            @ToolArg(description = "A concise, one-sentence summary of the migration. This will be used to build the final message.") String migrationSummary,
            @ToolArg(description = "A simple code snippet showing the code BEFORE the change.") String beforeExample,
            @ToolArg(description = "A simple code snippet showing the code AFTER the change.") String afterExample,
            @ToolArg(description = "The composed 'when' condition for the rule.") Condition when,
            @ToolArg(description = "The category of the rule.") Category category,
            @ToolArg(description = "The effort points (1-5).") Integer effort,
            @ToolArg(description = "A list of labels.") List<String> labels) {

        String message = buildMessage(migrationSummary, beforeExample, afterExample);
        
        return new Rule(
                ruleID,
                message,
                description,
                category,
                effort,
                labels,
                null, // tags
                List.of(), // customVariables
                when
        );
    }

    @Tool(description = "[CORE] Assembles the final Kantra rule object from its required parts, including optional links and variables.")
    public Rule buildRuleWithOptionalParams(
            @ToolArg(description = "A unique ID for the rule.") String ruleID,
            @ToolArg(description = "A short description of the rule.") String description,
            @ToolArg(description = "A concise, one-sentence summary of the migration. This will be used to build the final message.") String migrationSummary,
            @ToolArg(description = "A simple code snippet showing the code BEFORE the change.") String beforeExample,
            @ToolArg(description = "A simple code snippet showing the code AFTER the change.") String afterExample,
            @ToolArg(description = "The composed 'when' condition for the rule.") Condition when,
            @ToolArg(description = "The category of the rule.") Category category,
            @ToolArg(description = "The effort points (1-5).") Integer effort,
            @ToolArg(description = "A list of labels.") List<String> labels,
            @ToolArg(description = "A list of links to relevant documentation. If none are provided, pass an empty list.") List<Link> links,
            @ToolArg(description = "A list of custom variables for the message template. If none are provided, pass an empty list.") List<CustomVariable> customVariables) {

        String message = buildMessage(migrationSummary, beforeExample, afterExample);
        
        return new Rule(
                ruleID,
                message,
                description,
                category,
                effort,
                labels,
                null, // tags
                customVariables,
                when
        );
    }

    @Tool(description = "[CORE] Takes a fully assembled Rule object and converts it into a final YAML string.")
    public String serializeRuleToYaml(@ToolArg(description = "The complete Rule object to serialize.") Rule rule) {
        try {
            return ruleValidator.ruleToYaml(rule);
        } catch (Exception e) {
            return "Error: Could not serialize rule to YAML. " + e.getMessage();
        }
    }

    @Tool(description = "[CORE] Takes an existing Kantra rule in YAML format and enhances its message to be more detailed and actionable.")
    public String enhanceRule(
            @ToolArg(description = "The full YAML content of the existing Kantra rule.") String existingRuleYaml,
            @ToolArg(description = "A simple code snippet showing the code BEFORE the change.") String beforeExample,
            @ToolArg(description = "A simple code snippet showing the code AFTER the change.") String afterExample) {
        
        try {
            Rule existingRule = ruleValidator.parseYamlToRule(existingRuleYaml);
            
            // Enhance the message with examples
            String enhancedMessage = buildMessage(existingRule.message(), beforeExample, afterExample);
            
            Rule enhancedRule = new Rule(
                existingRule.ruleId(),
                enhancedMessage,
                existingRule.description(),
                existingRule.category(),
                existingRule.effort(),
                existingRule.labels(),
                existingRule.tag(),
                existingRule.customVariables(),
                existingRule.when()
            );
            
            return ruleValidator.ruleToYaml(enhancedRule);
        } catch (Exception e) {
            return "Error: Could not enhance rule. " + e.getMessage();
        }
    }

    @Tool(description = "[CORE] Creates a list of Konveyor-specific labels for Camel migration rules.")
    public List<String> createCamelMigrationLabels(
            @ToolArg(description = "The source Camel version (e.g., 'camel2').") String sourceVersion,
            @ToolArg(description = "The target Camel version (e.g., 'camel3+').") String targetVersion) {
        return List.of(
            "konveyor.io/source=" + sourceVersion,
            "konveyor.io/source=camel",
            "konveyor.io/target=" + targetVersion,
            "konveyor.io/target=camel"
        );
    }

    @Tool(description = "[CORE] Creates an empty list of custom variables for rules that don't need them.")
    public List<CustomVariable> createEmptyCustomVariables() {
        return List.of();
    }

    private String buildMessage(String migrationSummary, String beforeExample, String afterExample) {
        if (beforeExample != null && afterExample != null) {
            return String.format("%s\n\nBefore:\n```java\n%s\n```\n\nAfter:\n```java\n%s\n```", 
                migrationSummary, beforeExample, afterExample);
        }
        return migrationSummary;
    }
}