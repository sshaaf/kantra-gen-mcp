package dev.shaaf.kantra.rules.gen.commands.java;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.JavaLocation;
import dev.shaaf.kantra.rules.gen.model.JavaReferencedCondition;
import dev.shaaf.kantra.rules.gen.model.Rule;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a Kantra rule to detect Java class/method usage.
 * This is the primary Java rule command supporting all JavaLocation types.
 * 
 * Supports optional `annotated` field for matching annotations with specific element values.
 */
@ApplicationScoped
@RegisteredCommand
public class CreateJavaRuleCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.CREATE_JAVA_CLASS_RULE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"ruleID", "javaPattern", "location", "message", "category", "effort"};
    }

    @Override
    public String getDescription() {
        return "Create a Kantra rule to detect Java class usage (deprecated classes, method calls, annotations, etc.). " +
               "For ANNOTATION location, you can optionally specify 'annotated' with 'elements' to match specific annotation values.";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "ruleID": "struts-action-to-spring-controller",
                "javaPattern": "org.apache.struts.action.Action",
                "location": "INHERITANCE",
                "message": "Struts `Action` classes must be converted to Spring MVC `@Controller` classes.",
                "category": "MANDATORY",
                "effort": 5,
                "source": "struts",
                "target": "springboot",
                "links": [
                    {"title": "Spring MVC Controllers", "url": "https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller"}
                ],
                "annotated": {
                    "pattern": "optional-pattern-for-annotated",
                    "elements": [
                        {"name": "value", "value": ".*\\\\/$"}
                    ]
                }
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String ruleID = requireString(params, "ruleID");
        String javaPattern = requireString(params, "javaPattern");
        JavaLocation location = requireJavaLocation(params, "location");
        String message = requireString(params, "message");
        Category category = requireCategory(params, "category");
        int effort = requireInt(params, "effort");

        // Build the annotated condition if provided
        JavaReferencedCondition.Annotated annotated = buildAnnotated(params);

        JavaReferencedCondition condition;
        if (annotated != null) {
            condition = new JavaReferencedCondition(javaPattern, location.toString(), annotated);
        } else {
            condition = new JavaReferencedCondition(javaPattern, location.toString());
        }
        
        Rule rule = new Rule(
                ruleID,
                message,
                "Detects Java " + location.toString().toLowerCase().replace("_", " ") + ": " + javaPattern,
                category,
                effort,
                buildLabels(params),
                buildLinks(params),
                List.of(),
                List.of(),
                condition
        );
        return toYaml(rule);
    }
    
    /**
     * Build the Annotated condition from JSON parameters.
     * Expected format:
     * {
     *   "annotated": {
     *     "pattern": "optional-pattern",
     *     "elements": [
     *       {"name": "value", "value": ".*\\/$"}
     *     ]
     *   }
     * }
     * 
     * @param params JSON parameters
     * @return Annotated object or null if not provided
     */
    private JavaReferencedCondition.Annotated buildAnnotated(JsonNode params) {
        JsonNode annotatedNode = params.get("annotated");
        if (annotatedNode == null || annotatedNode.isNull()) {
            return null;
        }
        
        String pattern = null;
        if (annotatedNode.has("pattern") && !annotatedNode.get("pattern").isNull()) {
            pattern = annotatedNode.get("pattern").asText();
        }
        
        List<JavaReferencedCondition.Element> elements = null;
        JsonNode elementsNode = annotatedNode.get("elements");
        if (elementsNode != null && elementsNode.isArray()) {
            elements = new ArrayList<>();
            for (JsonNode elementNode : elementsNode) {
                String name = elementNode.has("name") ? elementNode.get("name").asText() : null;
                String value = elementNode.has("value") ? elementNode.get("value").asText() : null;
                if (name != null || value != null) {
                    elements.add(new JavaReferencedCondition.Element(name, value));
                }
            }
        }
        
        // Only return Annotated if we have at least pattern or elements
        if (pattern != null || (elements != null && !elements.isEmpty())) {
            return new JavaReferencedCondition.Annotated(pattern, elements);
        }
        
        return null;
    }
}

