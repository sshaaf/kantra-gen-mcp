package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.*;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;


/**
 * Unified facade providing access to all Kantra rule generation tools.
 * This class provides a single entry point for all rule generation operations
 * while maintaining the underlying separation of concerns.
 * 
 * Tool Categories:
 * - [CORE] - Rule assembly, serialization, and logical operations
 * - [JAVA] - Java-specific conditions (dependencies, references, annotations)
 * - [BUILTIN] - File system and content-based conditions
 * 
 * Cross-References:
 * - Use buildJavaReferenced() for Java code patterns
 * - Use buildFileCondition() for file name patterns
 * - Combine conditions with combineWithAnd() or combineWithOr()
 * - Assemble final rules with buildRule() or buildRuleWithOptionalParams()
 * - Generate YAML output with serializeRuleToYaml()
 */
@ApplicationScoped
public class KantraRuleToolsFacade {

    @Inject
    CoreRuleTools coreTools;
    
    @Inject
    JavaRuleTools javaTools;
    
    @Inject
    BuiltinRuleTools builtinTools;
    
    @Inject
    RuleValidator ruleValidator;

    // ========== CORE TOOLS - Rule Assembly & Management ==========
    
    @Tool(description = "[FACADE] Quick rule builder for simple Java migration rules. Creates a complete rule with Java reference condition and Konveyor labels.")
    public String buildJavaMigrationRule(
            @ToolArg(description = "A unique ID for the rule.") String ruleID,
            @ToolArg(description = "A short description of the rule.") String description,
            @ToolArg(description = "The migration message summary.") String migrationSummary,
            @ToolArg(description = "Java class/package pattern to match.") String javaPattern,
            @ToolArg(description = "Location type (IMPORT, CLASS, METHOD_CALL, etc.).") String location,
            @ToolArg(description = "Source version (e.g., 'camel2').") String sourceVersion,
            @ToolArg(description = "Target version (e.g., 'camel3+').") String targetVersion,
            @ToolArg(description = "Rule category.") Category category,
            @ToolArg(description = "Effort points (1-5).") Integer effort) {
        
        // Build Java condition
        Condition javaCondition = javaTools.buildJavaReferenced(location, javaPattern);
        
        // Create Konveyor labels
        List<String> labels = coreTools.createCamelMigrationLabels(sourceVersion, targetVersion);
        
        // Build the rule
        Rule rule = coreTools.buildRule(ruleID, description, migrationSummary, null, null, 
                                      javaCondition, category, effort, labels);
        
        // Serialize to YAML
        return coreTools.serializeRuleToYaml(rule);
    }
    
    @Tool(description = "[FACADE] Quick rule builder for file-based rules. Creates a complete rule with file condition.")
    public String buildFileMigrationRule(
            @ToolArg(description = "A unique ID for the rule.") String ruleID,
            @ToolArg(description = "A short description of the rule.") String description,
            @ToolArg(description = "The migration message summary.") String migrationSummary,
            @ToolArg(description = "File pattern to match (regex).") String filePattern,
            @ToolArg(description = "Rule category.") Category category,
            @ToolArg(description = "Effort points (1-5).") Integer effort,
            @ToolArg(description = "List of labels for the rule.") List<String> labels) {
        
        // Build file condition
        Condition fileCondition = builtinTools.buildFileCondition(filePattern);
        
        // Build the rule
        Rule rule = coreTools.buildRule(ruleID, description, migrationSummary, null, null,
                                      fileCondition, category, effort, labels);
        
        // Serialize to YAML
        return coreTools.serializeRuleToYaml(rule);
    }
    
    @Tool(description = "[FACADE] Validates a YAML rule string and returns validation results.")
    public RuleValidator.ValidationResult validateRule(
            @ToolArg(description = "YAML content to validate.") String yamlContent) {
        return ruleValidator.validateYamlRule(yamlContent);
    }
    
    @Tool(description = "[FACADE] Parses YAML content into a Rule object for inspection or modification.")
    public Rule parseYamlRule(@ToolArg(description = "YAML content to parse.") String yamlContent) {
        try {
            return ruleValidator.parseYamlToRule(yamlContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse YAML rule: " + e.getMessage(), e);
        }
    }

    // ========== DIRECT ACCESS TO SPECIALIZED TOOLS ==========
    
    @Tool(description = "[FACADE] Access to CoreRuleTools for rule assembly and serialization.")
    public CoreRuleTools getCoreTools() {
        return coreTools;
    }
    
    @Tool(description = "[FACADE] Access to JavaRuleTools for Java-specific conditions.")
    public JavaRuleTools getJavaTools() {
        return javaTools;
    }
    
    @Tool(description = "[FACADE] Access to BuiltinRuleTools for file and content-based conditions.")
    public BuiltinRuleTools getBuiltinTools() {
        return builtinTools;
    }
    
    // ========== CONVENIENCE METHODS ==========
    
    @Tool(description = "[FACADE] Creates a complex condition combining multiple Java patterns with OR logic.")
    public Condition createJavaOrCondition(
            @ToolArg(description = "List of Java patterns to match.") List<String> patterns,
            @ToolArg(description = "Location type for all patterns.") String location) {
        
        List<Condition> conditions = patterns.stream()
            .map(pattern -> javaTools.buildJavaReferenced(location, pattern))
            .toList();
            
        return coreTools.combineWithOr(conditions);
    }
    
    @Tool(description = "[FACADE] Creates a condition that matches files AND contains specific Java patterns.")
    public Condition createFileAndJavaCondition(
            @ToolArg(description = "File pattern to match.") String filePattern,
            @ToolArg(description = "Java pattern to find in the files.") String javaPattern,
            @ToolArg(description = "Java location type.") String location) {
        
        Condition fileCondition = builtinTools.buildFileCondition(filePattern);
        Condition javaCondition = javaTools.buildJavaReferenced(location, javaPattern);
        
        return coreTools.combineWithAnd(List.of(fileCondition, javaCondition));
    }
    
    @Tool(description = "[FACADE] Analyzes a user request and provides intelligent tool routing suggestions.")
    public String getToolSuggestions(@ToolArg(description = "User's request or description of what they want to accomplish") String userRequest) {
        String request = userRequest.toLowerCase();
        
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("# 🎯 Tool Routing Suggestions for: \"").append(userRequest).append("\"\n\n");
        
        // Analyze request and provide specific suggestions
        if (request.contains("java") && (request.contains("moved") || request.contains("migrate"))) {
            suggestions.append("""
                ## ✅ Recommended: Quick Java Migration
                ```
                buildJavaMigrationRule(
                    ruleID: "your-rule-id",
                    description: "Java element migration",
                    migrationSummary: "Describe what changed",
                    javaPattern: "com.old.package.*",
                    location: "IMPORT",  // or CLASS, METHOD_CALL, etc.
                    sourceVersion: "oldVersion",
                    targetVersion: "newVersion+", 
                    category: MANDATORY,
                    effort: 2
                )
                ```
                **Why:** Single call handles Java migrations with proper labels
                
                """);
        }
        
        if (request.contains("file") || request.contains("*.") || request.contains("xml") || request.contains("json")) {
            suggestions.append("""
                ## ✅ Recommended: File-Based Rule
                ```
                buildFileMigrationRule(
                    ruleID: "file-rule-001", 
                    description: "File pattern rule",
                    migrationSummary: "Update matching files",
                    filePattern: "*.xml",  // or your pattern
                    category: OPTIONAL,
                    effort: 1,
                    labels: ["config", "files"]
                )
                ```
                **Why:** Optimized for file pattern matching
                
                """);
        }
        
        if (request.contains("validate") || request.contains("check")) {
            suggestions.append("""
                ## ✅ Recommended: Validation Tools
                ```
                // Validate YAML
                result = validateRule(yamlContent)
                if (result.isValid()) {
                    // Parse for inspection
                    rule = parseYamlRule(yamlContent) 
                }
                ```
                **Why:** Direct validation and parsing support
                
                """);
        }
        
        if (request.contains("and") || request.contains("or") || request.contains("complex")) {
            suggestions.append("""
                ## ⚙️ Advanced: Complex Conditions
                ```
                // Step 1: Build individual conditions
                javaCondition = getJavaTools().buildJavaReferenced("IMPORT", "com.example.*")
                fileCondition = getBuiltinTools().buildFileCondition("*.java")
                
                // Step 2: Combine
                combined = getCoreTools().combineWithAnd([fileCondition, javaCondition])
                
                // Step 3: Build rule
                rule = getCoreTools().buildRule(...)
                yaml = getCoreTools().serializeRuleToYaml(rule)
                ```
                **Why:** Maximum flexibility for complex scenarios
                
                """);
        }
        
        // Always add general guidance
        suggestions.append("""
            ## 🧭 General Guidance
            - **Start Simple:** Try facade tools first ([FACADE] category)
            - **Progressive Complexity:** Move to direct tools only if needed
            - **Always Validate:** Use `validateRule()` on final YAML
            - **Get Help:** Use `getUsageExamples()` for comprehensive guide
            
            ## 🔄 Next Steps
            1. Choose the recommended approach above
            2. Customize parameters for your specific case
            3. Validate the generated YAML
            4. Test with your target application
            """);
        
        return suggestions.toString();
    }

    @Tool(description = "[FACADE] Gets comprehensive usage examples and cross-references for rule building workflows.")
    public String getUsageExamples() {
        return """
        # Kantra Rule Tools Usage Examples
        
        ## Common Workflows:
        
        ### 1. Simple Java Migration Rule:
        buildJavaMigrationRule(
            ruleID: "my-migration-001",
            description: "Class moved to new package",
            migrationSummary: "com.old.Class moved to com.new.Class",
            javaPattern: "com.old.Class",
            location: "IMPORT",
            sourceVersion: "version1",
            targetVersion: "version2+",
            category: MANDATORY,
            effort: 2
        )
        
        ### 2. Complex Condition Building:
        // Step 1: Build individual conditions
        javaCondition = getJavaTools().buildJavaReferenced("IMPORT", "com.example.*")
        fileCondition = getBuiltinTools().buildFileCondition("*.java")
        
        // Step 2: Combine conditions
        combinedCondition = getCoreTools().combineWithAnd([fileCondition, javaCondition])
        
        // Step 3: Build rule
        rule = getCoreTools().buildRule(...)
        
        // Step 4: Generate YAML
        yaml = getCoreTools().serializeRuleToYaml(rule)
        
        ### 3. Validation Workflow:
        // Validate YAML
        result = validateRule(yamlContent)
        if (result.isValid()) {
            // Parse for further processing
            rule = parseYamlRule(yamlContent)
        }
        
        ## Tool Categories:
        - [CORE] - Rule assembly, logical operations, serialization
        - [JAVA] - Java dependencies, references, annotations
        - [BUILTIN] - Files, XML, JSON, content matching
        - [FACADE] - Quick builders and convenience methods
        """;
    }
}
