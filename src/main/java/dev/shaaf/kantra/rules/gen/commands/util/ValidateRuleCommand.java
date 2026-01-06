package dev.shaaf.kantra.rules.gen.commands.util;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Validates a YAML rule file for syntax and schema compliance.
 */
@ApplicationScoped
@RegisteredCommand
public class ValidateRuleCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.VALIDATE_RULE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"yamlContent"};
    }

    @Override
    public String getDescription() {
        return "Validate a YAML rule file for syntax and schema compliance";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "yamlContent": "ruleID: test\\ncategory: mandatory\\nwhen:\\n  builtin.file:\\n    pattern: '*.java'\\n"
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String yamlContent = requireString(params, "yamlContent");
        
        RuleValidator.ValidationResult result = ruleValidator.validateYamlRule(yamlContent);
        if (result.isValid()) {
            return "Rule is valid!";
        } else {
            return "Rule validation failed: " + result.errors();
        }
    }
}

