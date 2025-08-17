package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JavaRuleTools {
    @Tool(description = "Builds a condition for various Java code references.")
    public WhenNode buildJavaReferenced(
            @ToolArg(description = "The Java location types, e.g., 'IMPORT', 'CLASS', 'METHOD_CALL'.") JavaLocation location,
            @ToolArg(description = "The pattern to match against the Java element.") String pattern,
            @ToolArg(description = "Optional: An annotation to check for on the reference.") Annotated annotated) {
        return new JavaReferenced(location, pattern, annotated, null);
    }

    @Tool(description = "Builds a condition to find a Java dependency.")
    public WhenNode buildJavaDependency(
            @ToolArg(description = "The exact name of the dependency.") String name,
            @ToolArg(description = "A regex to match against the dependency name.") String nameRegex,
            @ToolArg(description = "Optional: The upper version bound (inclusive).") String upperbound,
            @ToolArg(description = "Optional: The lower version bound (inclusive).") String lowerbound) {
        return new JavaDependency(name, nameRegex, upperbound, lowerbound);
    }
}