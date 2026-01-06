package dev.shaaf.kantra.rules.gen.commands.file;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.model.BuiltinFileContentCondition;
import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.Rule;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Creates a Kantra rule to detect content inside files (e.g., System.out.println, specific text patterns).
 */
@ApplicationScoped
@RegisteredCommand
public class CreateFileContentRuleCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.CREATE_FILE_CONTENT_RULE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"ruleID", "filePattern", "contentPattern", "message", "category", "effort"};
    }

    @Override
    public String getDescription() {
        return "Create a Kantra rule to detect content inside files (e.g., System.out.println, specific text patterns)";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "ruleID": "detect-system-out-println",
                "filePattern": "*.java",
                "contentPattern": "System\\\\.out\\\\.println",
                "message": "Replace System.out.println with proper logging. Before: System.out.println(\\"Hello\\"); After: logger.info(\\"Hello\\");",
                "category": "POTENTIAL",
                "effort": 1
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String ruleID = requireString(params, "ruleID");
        String filePattern = requireString(params, "filePattern");
        String contentPattern = requireString(params, "contentPattern");
        String message = requireString(params, "message");
        Category category = requireCategory(params, "category");
        int effort = requireInt(params, "effort");

        BuiltinFileContentCondition condition = new BuiltinFileContentCondition(filePattern, contentPattern);
        Rule rule = new Rule(
                ruleID,
                message,
                "Detects content in " + filePattern + ": " + contentPattern,
                category,
                effort,
                List.of("file-content", "pattern"),
                List.of(),
                List.of(),
                condition
        );
        return toYaml(rule);
    }
}

