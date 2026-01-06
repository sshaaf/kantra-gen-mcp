package dev.shaaf.kantra.rules.gen.commands.xml;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.model.BuiltinXmlCondition;
import dev.shaaf.kantra.rules.gen.model.Category;
import dev.shaaf.kantra.rules.gen.model.Rule;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a Kantra rule to detect XML content using XPath expressions.
 */
@ApplicationScoped
@RegisteredCommand
public class CreateXmlRuleCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.CREATE_XML_RULE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"ruleID", "xpath", "message", "category", "effort"};
    }

    @Override
    public String getDescription() {
        return "Create a Kantra rule to detect XML content using XPath expressions";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "ruleID": "deprecated-xml-config",
                "xpath": "//bean[@class='com.example.OldClass']",
                "message": "Update deprecated XML configuration. Before: <bean class='com.example.OldClass'/>; After: <bean class='com.example.NewClass'/>",
                "category": "MANDATORY",
                "effort": 2
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
        
        // Optional parameters for more specific XML matching
        String filepathsStr = optionalString(params, "filepaths", null);
        List<String> filepaths = filepathsStr != null ? Arrays.asList(filepathsStr.split(",")) : null;
        
        // Parse namespaces from JSON if provided
        Map<String, String> namespaces = null;
        if (params.has("namespaces") && !params.get("namespaces").isNull()) {
            namespaces = new HashMap<>();
            var nsNode = params.get("namespaces");
            var it = nsNode.fields();
            while (it.hasNext()) {
                var entry = it.next();
                namespaces.put(entry.getKey(), entry.getValue().asText());
            }
        }

        BuiltinXmlCondition condition = new BuiltinXmlCondition(filepaths, namespaces, xpath);
        Rule rule = new Rule(
                ruleID,
                message,
                "Detects XML content: " + xpath,
                category,
                effort,
                List.of("xml", "configuration"),
                List.of(),
                List.of(),
                condition
        );
        return toYaml(rule);
    }
}

