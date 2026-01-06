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
                "ruleID": "deprecated-thread-pool-policy",
                "javaPattern": "org.apache.camel.ThreadPoolRejectedPolicy",
                "location": "IMPORT",
                "message": "ThreadPoolRejectedPolicy moved. Before: import org.apache.camel.ThreadPoolRejectedPolicy; After: import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;",
                "category": "MANDATORY",
                "effort": 2
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
                List.of("java", location.toString().toLowerCase().replace("_", "-")),
                List.of(),
                List.of(),
                condition
        );
        return toYaml(rule);
    }
}

