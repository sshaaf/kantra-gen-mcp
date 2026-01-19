package dev.shaaf.kantra.rules.gen.commands.ruleset;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a ruleset.yaml file for bundling Kantra rules together.
 * The ruleset.yaml is required for the Kantra analyzer to discover and organize rules.
 */
@ApplicationScoped
@RegisteredCommand
public class CreateRulesetCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.CREATE_RULESET;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"name"};
    }

    @Override
    public String getDescription() {
        return "Create a ruleset.yaml file for bundling Kantra rules. " +
               "The ruleset defines metadata like name, description, and labels that apply to all rules in the directory.";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "name": "struts-to-springboot",
                "description": "Migration rules for converting Struts 1.x to Spring Boot",
                "source": "struts",
                "target": "springboot"
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String name = requireString(params, "name");
        String description = optionalString(params, "description", null);
        List<String> labels = buildLabels(params);
        
        // Also support explicit labels array
        if (params.has("labels") && params.get("labels").isArray()) {
            for (JsonNode labelNode : params.get("labels")) {
                String label = labelNode.asText();
                if (!labels.contains(label)) {
                    labels.add(label);
                }
            }
        }

        // Build the ruleset YAML using ordered map to control output order
        Map<String, Object> rulesetMap = new LinkedHashMap<>();
        rulesetMap.put("name", name);
        
        if (description != null && !description.isBlank()) {
            rulesetMap.put("description", description);
        }
        
        if (!labels.isEmpty()) {
            rulesetMap.put("labels", labels);
        }

        // Configure YAML output
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setIndent(2);
        options.setIndicatorIndent(0);
        
        Yaml yaml = new Yaml(options);
        return yaml.dump(rulesetMap);
    }
}
