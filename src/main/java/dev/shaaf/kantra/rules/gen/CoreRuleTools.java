package dev.shaaf.kantra.rules.gen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import dev.shaaf.kantra.rules.gen.model.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CoreRuleTools {
    private final ObjectMapper yamlMapper = new ObjectMapper(
            new YAMLFactory()
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                    .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID));

    @Tool(description = "Combines multiple conditions with a logical AND.")
    public WhenNode combineWithAnd(@ToolArg(description = "A list of conditions to combine.") List<WhenNode> conditions) {
        return new AndNode(conditions);
    }

    @Tool(description = "Combines multiple conditions with a logical OR.")
    public WhenNode combineWithOr(@ToolArg(description = "A list of conditions to combine.") List<WhenNode> conditions) {
        return new OrNode(conditions);
    }

    @Tool(description = "Assembles the final Kantra rule object.")
    public KantraRule buildRule(
            @ToolArg(description = "A unique ID for the rule.") String ruleID,
            @ToolArg(description = "A short description of the rule.") String description,
            @ToolArg(description = "Give a step-by-step instruction on changing the code. So an AI can help find a solution to this detected migration scenario.") String message,
            @ToolArg(description = "The composed 'when' condition for the rule.") WhenNode when,
            @ToolArg(description = "The category of the rule.") Category category,
            @ToolArg(description = "The effort points (1-5).") Integer effort,
            @ToolArg(description = "A list of labels.") List<String> labels,
            @ToolArg(description = "A list of links.") List<Link> links,
            @ToolArg(description = "A list of custom variables.") List<CustomVariable> customVariables) {
        return new KantraRule(ruleID, description, message, category, effort, labels, links, customVariables, when);
    }

    @Tool(description = "Takes a fully assembled KantraRule object and converts it into a final YAML string. This should be the last step.")
    public String serializeRuleToYaml(@ToolArg(description = "The complete KantraRule object to serialize.") KantraRule rule) {
        try {
            return yamlMapper.writeValueAsString(List.of(rule));
        } catch (JsonProcessingException e) {
            return "Error: Could not serialize rule to YAML. " + e.getMessage();
        }
    }
}
