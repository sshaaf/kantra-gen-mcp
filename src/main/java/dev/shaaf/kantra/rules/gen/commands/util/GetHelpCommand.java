package dev.shaaf.kantra.rules.gen.commands.util;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.CommandRegistry;
import dev.shaaf.kantra.rules.gen.commands.KantraCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Provides help and examples for creating Kantra rules.
 */
@ApplicationScoped
@RegisteredCommand
public class GetHelpCommand extends AbstractCommand {

    @Inject
    CommandRegistry registry;

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.GET_HELP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{};
    }

    @Override
    public String getDescription() {
        return "Get help and examples for creating Kantra rules";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "topic": "java"
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String topic = optionalString(params, "topic", "general").toLowerCase();

        return switch (topic) {
            case "java", "import", "class", "annotation" -> getJavaHelp();
            case "file", "content", "filecontent" -> getFileContentHelp();
            case "xml" -> getXmlHelp();
            case "json" -> getJsonHelp();
            case "operations", "ops" -> getOperationsHelp();
            default -> getGeneralHelp();
        };
    }

    private String getGeneralHelp() {
        return """
            ## Kantra Rule Generation Help
            
            Use the `executeKantraOperation` tool with an operation and JSON params.
            
            ### Available Topics
            - `topic: "java"` - Java rule examples
            - `topic: "file"` - File content rule examples
            - `topic: "xml"` - XML rule examples
            - `topic: "json"` - JSON rule examples
            - `topic: "operations"` - List all available operations
            
            ### Quick Start
            ```json
            {
                "operation": "CREATE_JAVA_CLASS_RULE",
                "params": {
                    "ruleID": "javax-to-jakarta",
                    "javaPattern": "javax.persistence.*",
                    "location": "IMPORT",
                    "message": "Migrate to Jakarta EE. Before: import javax.persistence.Entity; After: import jakarta.persistence.Entity;",
                    "category": "MANDATORY",
                    "effort": 1
                }
            }
            ```
            """;
    }

    private String getJavaHelp() {
        return """
            ## Java Rule Examples
            
            ### CREATE_JAVA_CLASS_RULE
            Supports all JavaLocation types:
            - IMPORT, CLASS, METHOD_CALL, CONSTRUCTOR_CALL
            - ANNOTATION, FIELD, METHOD, INHERITANCE
            - IMPLEMENTS_TYPE, ENUM, RETURN_TYPE
            - VARIABLE_DECLARATION, TYPE, PACKAGE
            
            **Example - Import Migration:**
            ```json
            {
                "operation": "CREATE_JAVA_CLASS_RULE",
                "params": {
                    "ruleID": "javax-to-jakarta-persistence",
                    "javaPattern": "javax.persistence.*",
                    "location": "IMPORT",
                    "message": "Migrate from javax to jakarta. Before: import javax.persistence.Entity; After: import jakarta.persistence.Entity;",
                    "category": "MANDATORY",
                    "effort": 1
                }
            }
            ```
            
            **Example - Annotation Migration:**
            ```json
            {
                "operation": "CREATE_JAVA_CLASS_RULE",
                "params": {
                    "ruleID": "ejb-stateless-to-cdi",
                    "javaPattern": "javax.ejb.Stateless",
                    "location": "ANNOTATION",
                    "message": "Replace EJB @Stateless with CDI. Before: @Stateless public class MyBean; After: @ApplicationScoped public class MyBean;",
                    "category": "MANDATORY",
                    "effort": 2
                }
            }
            ```
            
            **Example - Constructor Call:**
            ```json
            {
                "operation": "CREATE_JAVA_CLASS_RULE",
                "params": {
                    "ruleID": "replace-vector",
                    "javaPattern": "java.util.Vector",
                    "location": "CONSTRUCTOR_CALL",
                    "message": "Replace Vector with ArrayList. Before: new Vector<>(); After: new ArrayList<>();",
                    "category": "POTENTIAL",
                    "effort": 1
                }
            }
            ```
            """;
    }

    private String getFileContentHelp() {
        return """
            ## File Content Rule Examples
            
            ### CREATE_FILE_CONTENT_RULE
            Detects text patterns inside files.
            
            **Example - System.out.println Detection:**
            ```json
            {
                "operation": "CREATE_FILE_CONTENT_RULE",
                "params": {
                    "ruleID": "detect-system-out",
                    "filePattern": "*.java",
                    "contentPattern": "System\\\\.out\\\\.println",
                    "message": "Replace with logger. Before: System.out.println(\\"msg\\"); After: logger.info(\\"msg\\");",
                    "category": "POTENTIAL",
                    "effort": 1
                }
            }
            ```
            
            ### CREATE_FILE_RULE
            Detects files by pattern.
            
            **Example - Detect Config File:**
            ```json
            {
                "operation": "CREATE_FILE_RULE",
                "params": {
                    "ruleID": "detect-old-config",
                    "filePattern": "applicationContext.xml",
                    "message": "Migrate XML config to Java. Before: applicationContext.xml; After: @Configuration class",
                    "category": "MANDATORY",
                    "effort": 3
                }
            }
            ```
            """;
    }

    private String getXmlHelp() {
        return """
            ## XML Rule Examples
            
            ### CREATE_XML_RULE
            Uses XPath to detect XML elements.
            
            **Example - Detect Spring Bean:**
            ```json
            {
                "operation": "CREATE_XML_RULE",
                "params": {
                    "ruleID": "deprecated-spring-bean",
                    "xpath": "//bean[@class='com.example.OldService']",
                    "message": "Update deprecated bean. Before: <bean class='com.example.OldService'/>; After: <bean class='com.example.NewService'/>",
                    "category": "MANDATORY",
                    "effort": 2
                }
            }
            ```
            
            **Example - Detect Maven Dependency:**
            ```json
            {
                "operation": "CREATE_XML_RULE",
                "params": {
                    "ruleID": "old-maven-dependency",
                    "xpath": "//dependency[artifactId='old-library']",
                    "message": "Update dependency. Before: <artifactId>old-library</artifactId>; After: <artifactId>new-library</artifactId>",
                    "category": "MANDATORY",
                    "effort": 1
                }
            }
            ```
            """;
    }

    private String getJsonHelp() {
        return """
            ## JSON Rule Examples
            
            ### CREATE_JSON_RULE
            Uses JSONPath to detect JSON elements.
            
            **Example - Detect JSON Config:**
            ```json
            {
                "operation": "CREATE_JSON_RULE",
                "params": {
                    "ruleID": "deprecated-json-setting",
                    "xpath": "$.settings.deprecated",
                    "message": "Remove deprecated setting. Before: 'deprecated': true; After: Remove the key",
                    "category": "OPTIONAL",
                    "effort": 1
                }
            }
            ```
            """;
    }

    private String getOperationsHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Available Operations\n\n");
        sb.append(registry.getAvailableOperationsByCategory());
        sb.append("\n\n### Operation Details\n\n");
        
        for (KantraOperation op : registry.getAvailableOperations()) {
            KantraCommand cmd = registry.getCommand(op);
            if (cmd != null) {
                sb.append("**").append(op.name()).append("**\n");
                sb.append("- Description: ").append(cmd.getDescription()).append("\n");
                sb.append("- Required params: ").append(String.join(", ", cmd.getRequiredParams())).append("\n\n");
            }
        }
        
        return sb.toString();
    }
}

