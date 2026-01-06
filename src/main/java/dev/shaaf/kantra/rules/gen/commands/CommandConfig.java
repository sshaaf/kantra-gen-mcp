package dev.shaaf.kantra.rules.gen.commands;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.List;
import java.util.Optional;

/**
 * Configuration for command loading and filtering.
 * Allows enabling/disabling commands via application.properties.
 * 
 * Example configuration:
 * <pre>
 * kantra.mcp.commands.enabled=CREATE_JAVA_CLASS_RULE,CREATE_FILE_CONTENT_RULE
 * kantra.mcp.commands.disabled=GET_HELP
 * kantra.mcp.commands.enable-all-by-default=true
 * kantra.mcp.commands.log-on-startup=true
 * </pre>
 */
@ConfigMapping(prefix = "kantra.mcp.commands")
public interface CommandConfig {

    /**
     * Explicitly enabled commands. If set, ONLY these commands are available.
     * Example: CREATE_JAVA_CLASS_RULE,CREATE_FILE_CONTENT_RULE
     */
    Optional<List<String>> enabled();

    /**
     * Explicitly disabled commands. These are excluded even if discovered.
     * Example: GET_HELP,VALIDATE_RULE
     */
    Optional<List<String>> disabled();

    /**
     * Enable all discovered commands by default.
     * Set to false to require explicit enablement via the 'enabled' list.
     */
    @WithDefault("true")
    boolean enableAllByDefault();

    /**
     * Log available commands on startup.
     */
    @WithDefault("true")
    boolean logOnStartup();
}

