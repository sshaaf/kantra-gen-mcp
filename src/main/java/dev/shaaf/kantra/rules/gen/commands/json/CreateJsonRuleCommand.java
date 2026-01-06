package dev.shaaf.kantra.rules.gen.commands.json;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.model.BuiltinJsonCondition;
import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.Rule;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

/**
 * Creates a Kantra rule to detect JSON content using JSONPath expressions.
 */
@ApplicationScoped
@RegisteredCommand
public class CreateJsonRuleCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.CREATE_JSON_RULE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"ruleID", "xpath", "message", "category", "effort"};
    }

    @Override
    public String getDescription() {
        return "Create a Kantra rule to detect JSON content using JSONPath expressions";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "ruleID": "deprecated-json-config",
                "xpath": "$.config.deprecated",
                "message": "Update deprecated JSON configuration. Before: 'deprecated': true; After: Remove deprecated field",
                "category": "OPTIONAL",
                "effort": 1
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String ruleID = requireString(params, "ruleID");
        String xpath = requireString(params, "xpath");
        String message = requireString(params, "message");
        Category category = requireCategory(params, "category");
        int effort = requireInt(params, "effort");
        
        // Optional parameters
        String filepathsStr = optionalString(params, "filepaths", null);
        List<String> filepaths = filepathsStr != null ? Arrays.asList(filepathsStr.split(",")) : null;

        BuiltinJsonCondition condition = new BuiltinJsonCondition(filepaths, xpath);
        Rule rule = new Rule(
                ruleID,
                message,
                "Detects JSON content: " + xpath,
                category,
                effort,
                List.of("json", "configuration"),
                List.of(),
                List.of(),
                condition
        );
        return toYaml(rule);
    }
}

