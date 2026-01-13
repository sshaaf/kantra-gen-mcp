package dev.shaaf.kantra.rules.gen.commands.file;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.model.BuiltinFileCondition;
import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.Rule;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Creates a Kantra rule to detect files by pattern (e.g., *.xml, *.java).
 */
@ApplicationScoped
@RegisteredCommand
public class CreateFileRuleCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.CREATE_FILE_RULE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"ruleID", "filePattern", "message", "category", "effort"};
    }

    @Override
    public String getDescription() {
        return "Create a Kantra rule to detect files by pattern (e.g., *.xml, *.java, specific file names)";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "ruleID": "detect-old-config-file",
                "filePattern": "applicationContext.xml",
                "message": "Migrate XML configuration to Java config. Before: applicationContext.xml; After: @Configuration class",
                "category": "MANDATORY",
                "effort": 3,
                "source": "spring-xml",
                "target": "springboot"
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String ruleID = requireString(params, "ruleID");
        String filePattern = requireString(params, "filePattern");
        String message = requireString(params, "message");
        Category category = requireCategory(params, "category");
        int effort = requireInt(params, "effort");

        BuiltinFileCondition condition = new BuiltinFileCondition(filePattern);
        Rule rule = new Rule(
                ruleID,
                message,
                "Detects files matching: " + filePattern,
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

