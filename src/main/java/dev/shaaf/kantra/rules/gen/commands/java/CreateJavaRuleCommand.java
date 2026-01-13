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

import java.util.List;

/**
 * Creates a Kantra rule to detect Java class/method usage.
 * This is the primary Java rule command supporting all JavaLocation types.
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
        return "Create a Kantra rule to detect Java class usage (deprecated classes, method calls, annotations, etc.)";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "ruleID": "struts-action-to-spring-controller",
                "javaPattern": "org.apache.struts.action.Action",
                "location": "INHERITANCE",
                "message": "Struts `Action` classes must be converted to Spring MVC `@Controller` classes.\\n\\n**Before (Struts Action):**\\n```java\\npublic class ListItemsAction extends Action {\\n    public ActionForward execute(...) { ... }\\n}\\n```\\n\\n**After (Spring Controller):**\\n```java\\n@Controller\\n@RequestMapping(\\"/items\\")\\npublic class ItemController {\\n    @GetMapping\\n    public String listItems(Model model) { ... }\\n}\\n```",
                "category": "MANDATORY",
                "effort": 5,
                "source": "struts",
                "target": "springboot",
                "links": [
                    {"title": "Spring MVC Controllers", "url": "https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller"}
                ]
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

        JavaReferencedCondition condition = new JavaReferencedCondition(javaPattern, location.toString());
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
}

