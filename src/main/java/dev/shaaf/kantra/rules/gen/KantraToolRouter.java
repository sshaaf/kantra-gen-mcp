package dev.shaaf.kantra.rules.gen;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Intelligent tool router that analyzes user requests and recommends the best tool path
 * for Kantra rule generation.
 */
@ApplicationScoped
public class KantraToolRouter {

    @Tool(description = "[ROUTER] Analyzes a user request and recommends the best tool path for generating Kantra rules.")
    public ToolRoutingRecommendation routeUserRequest(
            @ToolArg(description = "The user's request for rule generation") String userRequest) {
        
        String request = userRequest.toLowerCase();
        
        // Analyze request patterns
        if (isSimpleJavaMigration(request)) {
            return createJavaMigrationRecommendation(request);
        } else if (isFileBased(request)) {
            return createFileBasedRecommendation(request);
        } else if (isValidationRequest(request)) {
            return createValidationRecommendation();
        } else if (isComplexCondition(request)) {
            return createComplexConditionRecommendation(request);
        } else {
            return createGeneralRecommendation(request);
        }
    }

    @Tool(description = "[ROUTER] Provides a complete workflow guide for a specific type of Kantra rule generation.")
    public String getWorkflowGuide(
            @ToolArg(description = "Type of workflow: java-migration, file-based, complex-condition, validation") 
            String workflowType) {
        
        return switch (workflowType.toLowerCase()) {
            case "java-migration" -> """
                # Java Migration Workflow
                
                ## Quick Path (Recommended):
                ```
                KantraRuleToolsFacade.buildJavaMigrationRule(
                    ruleID: "your-rule-id",
                    description: "Brief description", 
                    migrationSummary: "What changed",
                    javaPattern: "com.old.Class",
                    location: "IMPORT", 
                    sourceVersion: "v1",
                    targetVersion: "v2+",
                    category: MANDATORY,
                    effort: 2
                )
                ```
                
                ## Custom Path (More Control):
                1. `JavaRuleTools.buildJavaReferenced(location, pattern)`
                2. `CoreRuleTools.createCamelMigrationLabels(source, target)` 
                3. `CoreRuleTools.buildRule(...)`
                4. `CoreRuleTools.serializeRuleToYaml(rule)`
                """;
                
            case "file-based" -> """
                # File-Based Rule Workflow
                
                ## Quick Path:
                ```
                KantraRuleToolsFacade.buildFileMigrationRule(
                    ruleID: "file-rule-001",
                    description: "File pattern rule",
                    migrationSummary: "Update configuration files", 
                    filePattern: "*.xml",
                    category: OPTIONAL,
                    effort: 1,
                    labels: ["config", "xml"]
                )
                ```
                
                ## Custom Path:
                1. `BuiltinRuleTools.buildFileCondition(pattern)`
                2. Optionally combine with content: `buildFileContentCondition()`
                3. `CoreRuleTools.buildRule(...)`
                4. `CoreRuleTools.serializeRuleToYaml(rule)`
                """;
                
            case "complex-condition" -> """
                # Complex Condition Workflow
                
                ## Step-by-Step Process:
                1. **Build Individual Conditions:**
                   - `JavaRuleTools.buildJavaReferenced()` for Java elements
                   - `BuiltinRuleTools.buildFileCondition()` for files
                   - `BuiltinRuleTools.buildBuiltinXmlCondition()` for XML
                
                2. **Combine Conditions:**
                   - `CoreRuleTools.combineWithAnd([condition1, condition2])`
                   - `CoreRuleTools.combineWithOr([condition1, condition2])`
                
                3. **Assemble Rule:**
                   - `CoreRuleTools.buildRule()` or `buildRuleWithOptionalParams()`
                
                4. **Generate Output:**
                   - `CoreRuleTools.serializeRuleToYaml(rule)`
                
                5. **Validate:**
                   - `KantraRuleToolsFacade.validateRule(yaml)`
                """;
                
            case "validation" -> """
                # Validation Workflow
                
                ## Validate Existing YAML:
                ```
                result = KantraRuleToolsFacade.validateRule(yamlContent)
                if (!result.isValid()) {
                    // Handle errors: result.errors()
                }
                ```
                
                ## Parse for Inspection:
                ```
                rule = KantraRuleToolsFacade.parseYamlRule(yamlContent)
                // Inspect rule properties
                ```
                
                ## Enhance Existing Rule:
                ```
                enhancedYaml = CoreRuleTools.enhanceRule(
                    existingYaml, beforeExample, afterExample
                )
                ```
                """;
                
            default -> """
                # General Kantra Rule Generation Workflow
                
                ## Decision Tree:
                1. **Simple Java migration?** → Use `buildJavaMigrationRule()`
                2. **File-based rule?** → Use `buildFileMigrationRule()`  
                3. **Need validation?** → Use `validateRule()`
                4. **Complex conditions?** → Use step-by-step approach
                
                ## Always Remember:
                - Start with facade tools for common patterns
                - Use direct tools for fine control
                - Validate final YAML output
                - Include appropriate labels and effort levels
                """;
        };
    }

    private boolean isSimpleJavaMigration(String request) {
        return (request.contains("java") || request.contains("class") || request.contains("import") || 
                request.contains("package")) && 
               (request.contains("moved") || request.contains("migrate") || request.contains("change"));
    }

    private boolean isFileBased(String request) {
        return request.contains("file") && !request.contains("java") ||
               request.contains("*.") || request.contains("xml") || request.contains("json") ||
               request.contains("configuration");
    }

    private boolean isValidationRequest(String request) {
        return request.contains("validate") || request.contains("check") || 
               request.contains("yaml") && !request.contains("generate");
    }

    private boolean isComplexCondition(String request) {
        return request.contains("and") || request.contains("or") || 
               request.contains("both") || request.contains("either") ||
               request.contains("combine") || request.contains("complex");
    }

    private ToolRoutingRecommendation createJavaMigrationRecommendation(String request) {
        return new ToolRoutingRecommendation(
            "java-migration",
            "KantraRuleToolsFacade.buildJavaMigrationRule",
            "Use the quick Java migration builder for simple Java element migrations",
            """
            Recommended approach:
            1. Use buildJavaMigrationRule() with:
               - Clear ruleID
               - Descriptive message
               - Correct Java pattern and location
               - Appropriate source/target versions
            2. This will automatically handle labels and YAML generation
            """,
            1
        );
    }

    private ToolRoutingRecommendation createFileBasedRecommendation(String request) {
        return new ToolRoutingRecommendation(
            "file-based",
            "KantraRuleToolsFacade.buildFileMigrationRule",
            "Use the quick file-based rule builder for file pattern matching",
            """
            Recommended approach:
            1. Use buildFileMigrationRule() with:
               - File pattern (regex)
               - Appropriate category and effort
               - Relevant labels
            2. Consider combining with content conditions if needed
            """,
            1
        );
    }

    private ToolRoutingRecommendation createValidationRecommendation() {
        return new ToolRoutingRecommendation(
            "validation",
            "KantraRuleToolsFacade.validateRule",
            "Use validation tools to check YAML rule correctness",
            """
            Recommended approach:
            1. Use validateRule() to check YAML syntax and structure
            2. Use parseYamlRule() if you need to inspect the parsed rule
            3. Check result.isValid() and handle result.errors() appropriately
            """,
            1
        );
    }

    private ToolRoutingRecommendation createComplexConditionRecommendation(String request) {
        return new ToolRoutingRecommendation(
            "complex-condition",
            "Multi-step workflow using CoreRuleTools, JavaRuleTools, BuiltinRuleTools",
            "Build complex conditions step-by-step using direct tools",
            """
            Recommended approach:
            1. Break down requirements into individual conditions
            2. Build each condition using appropriate tools:
               - JavaRuleTools for Java elements
               - BuiltinRuleTools for files/content
            3. Combine using combineWithAnd() or combineWithOr()
            4. Assemble with buildRule()
            5. Generate YAML with serializeRuleToYaml()
            6. Validate the result
            """,
            4
        );
    }

    private ToolRoutingRecommendation createGeneralRecommendation(String request) {
        return new ToolRoutingRecommendation(
            "general",
            "Analyze request further or use KantraRuleToolsFacade.getUsageExamples",
            "Request needs clarification or doesn't match common patterns",
            """
            Suggested approach:
            1. Get more details about the specific requirements
            2. Use getUsageExamples() to see common patterns
            3. Start with facade tools if possible
            4. Break down complex requests into smaller parts
            """,
            2
        );
    }

    public record ToolRoutingRecommendation(
        String requestType,
        String recommendedTool,
        String reasoning,
        String workflow,
        int complexityLevel
    ) {}
}
