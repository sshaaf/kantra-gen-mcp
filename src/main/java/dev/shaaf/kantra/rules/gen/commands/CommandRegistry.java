package dev.shaaf.kantra.rules.gen.commands;

import dev.shaaf.kantra.rules.gen.KantraOperation;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry for all Kantra commands.
 * Discovers commands via CDI and filters them based on configuration.
 */
@ApplicationScoped
@Startup
public class CommandRegistry {

    private final Map<KantraOperation, KantraCommand> commands = new EnumMap<>(KantraOperation.class);
    private final Set<KantraOperation> availableOperations = EnumSet.noneOf(KantraOperation.class);

    @Inject
    @RegisteredCommand
    Instance<KantraCommand> discoveredCommands;

    @Inject
    CommandConfig config;

    @PostConstruct
    void initialize() {
        // Collect all discovered commands
        Map<KantraOperation, KantraCommand> discovered = new EnumMap<>(KantraOperation.class);
        for (KantraCommand command : discoveredCommands) {
            discovered.put(command.getOperation(), command);
        }

        Log.infof("Discovered %d commands", discovered.size());

        // Apply configuration filters
        Set<String> disabled = config.disabled()
                .map(HashSet::new)
                .orElseGet(HashSet::new);
        Optional<List<String>> explicitlyEnabled = config.enabled();

        for (var entry : discovered.entrySet()) {
            KantraOperation op = entry.getKey();
            String opName = op.name();

            // Check if disabled
            if (disabled.contains(opName)) {
                Log.debugf("Command %s is disabled via configuration", opName);
                continue;
            }

            // Check if explicitly enabled (if explicit list is provided)
            if (explicitlyEnabled.isPresent()) {
                if (!explicitlyEnabled.get().contains(opName)) {
                    Log.debugf("Command %s not in enabled list, skipping", opName);
                    continue;
                }
            } else if (!config.enableAllByDefault()) {
                // If enableAllByDefault is false and no explicit list, skip
                Log.debugf("Command %s skipped (enableAllByDefault=false)", opName);
                continue;
            }

            // Register the command
            commands.put(op, entry.getValue());
            availableOperations.add(op);
        }

        // Log available commands
        if (config.logOnStartup()) {
            logAvailableCommands();
        }
    }

    private void logAvailableCommands() {
        Log.info("═══════════════════════════════════════════════════════════");
        Log.info("  Kantra MCP Server - Available Commands");
        Log.info("═══════════════════════════════════════════════════════════");

        // Group by category
        Map<String, List<KantraOperation>> byCategory = availableOperations.stream()
                .collect(Collectors.groupingBy(this::getCategory));

        byCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Log.infof("  %s:", entry.getKey());
                    entry.getValue().stream()
                            .sorted()
                            .forEach(op -> Log.infof("    - %s", op.name()));
                });

        Log.infof("  Total: %d commands enabled", commands.size());
        Log.info("═══════════════════════════════════════════════════════════");
    }

    private String getCategory(KantraOperation op) {
        String name = op.name();
        if (name.startsWith("CREATE_JAVA")) return "Java Rules";
        if (name.contains("FILE_CONTENT") || name.contains("PROPERTIES")) return "File Content Rules";
        if (name.contains("XML")) return "XML Rules";
        if (name.contains("JSON")) return "JSON Rules";
        if (name.contains("FILE") || name.contains("TAGS")) return "Built-in Rules";
        if (name.contains("VALIDATE") || name.contains("HELP") || name.contains("COMBINE")) return "Utilities";
        return "Other";
    }

    /**
     * Get command for an operation.
     *
     * @param operation The operation to look up
     * @return Command or null if not available
     */
    public KantraCommand getCommand(KantraOperation operation) {
        return commands.get(operation);
    }

    /**
     * Check if an operation is available.
     *
     * @param operation The operation to check
     * @return true if the operation is enabled
     */
    public boolean isAvailable(KantraOperation operation) {
        return commands.containsKey(operation);
    }

    /**
     * Get all available operations.
     *
     * @return Unmodifiable set of available operations
     */
    public Set<KantraOperation> getAvailableOperations() {
        return Collections.unmodifiableSet(availableOperations);
    }

    /**
     * Get available operations as comma-separated string (for tool description).
     *
     * @return Comma-separated list of operation names
     */
    public String getAvailableOperationsString() {
        return availableOperations.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Get available operations grouped by category for help text.
     *
     * @return Formatted string with operations by category
     */
    public String getAvailableOperationsByCategory() {
        StringBuilder sb = new StringBuilder();
        Map<String, List<KantraOperation>> byCategory = availableOperations.stream()
                .collect(Collectors.groupingBy(this::getCategory));
        
        byCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append(entry.getKey()).append(": ");
                    sb.append(entry.getValue().stream()
                            .map(Enum::name)
                            .sorted()
                            .collect(Collectors.joining(", ")));
                    sb.append("; ");
                });
        
        return sb.toString().trim();
    }

    /**
     * Get the count of available commands.
     *
     * @return Number of enabled commands
     */
    public int getCommandCount() {
        return commands.size();
    }
}

