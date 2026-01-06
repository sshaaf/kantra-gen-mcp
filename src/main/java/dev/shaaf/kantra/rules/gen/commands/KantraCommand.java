package dev.shaaf.kantra.rules.gen.commands;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;

/**
 * Interface for all Kantra MCP commands.
 * Implement this interface and annotate with @RegisteredCommand
 * to auto-register a new command.
 */
public interface KantraCommand {

    /**
     * The operation this command handles.
     */
    KantraOperation getOperation();

    /**
     * Execute the command with given parameters.
     *
     * @param params JSON parameters from the MCP request
     * @return Result string (typically YAML or message)
     * @throws Exception if execution fails
     */
    String execute(JsonNode params) throws Exception;

    /**
     * Human-readable description for documentation.
     */
    default String getDescription() {
        return "Executes " + getOperation().name();
    }

    /**
     * Required parameter names for validation.
     */
    default String[] getRequiredParams() {
        return new String[0];
    }

    /**
     * Example parameters JSON for help documentation.
     */
    default String getExampleParams() {
        return "{}";
    }
}

