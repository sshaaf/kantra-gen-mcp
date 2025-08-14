package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.core.model.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CoreRuleTools {

    @Tool(description = "Combines multiple conditions using a logical AND.")
    public Condition combineWithAnd(@ToolArg(description = "A list of conditions to combine.") List<Condition> conditions) {
        return new AndCondition(conditions);
    }

    @Tool(description = "Combines multiple conditions using a logical OR.")
    public Condition combineWithOr(@ToolArg(description = "A list of conditions to combine.") List<Condition> conditions) {
        return new OrCondition(conditions);
    }

    @Tool(description = "Negates a given condition.")
    public Condition combineWithNot(@ToolArg(description = "The condition to negate.") Condition condition) {
        return new NotCondition(true, condition);
    }

    @Tool(description = "Assembles the final Kantra rule object from its parts.")
    public KantraRule buildRule(
            @ToolArg(description = "A unique ID for the rule.") String ruleID,
            @ToolArg(description = "A short description of the rule.") String description,
            @ToolArg(description = "The detailed message to display for an incident.") String message,
            @ToolArg(description = "The composed 'when' condition for the rule.") Condition when,
            @ToolArg(description = "The category of the rule: POTENTIAL, OPTIONAL, or MANDATORY.") Category category,
            @ToolArg(description = "The effort points (1-5) to fix the issue.") Integer effort,
            @ToolArg(description = "A list of labels for categorization.") List<String> labels,
            @ToolArg(description = "A list of links to relevant documentation.") List<Link> links,
            @ToolArg(description = "A list of custom variables for the message template.") List<CustomVariable> customVariables) {

        return new KantraRule(
                ruleID,
                description,
                message,
                category,
                effort,
                labels,
                links,
                customVariables,
                when
        );
    }
}
