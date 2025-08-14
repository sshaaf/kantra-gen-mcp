package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.core.model.Condition;
import dev.shaaf.kantra.rules.gen.java.model.JavaDependencyCondition;
import dev.shaaf.kantra.rules.gen.java.model.JavaReferencedCondition;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JavaRuleTools {

    @Tool(description = "Builds a condition for Java type declarations. Use for locations: class, enum, implements_type, inheritance.")
    public Condition buildTypeDeclarationCondition(
            @ToolArg(description = "The location type: class, enum, implements_type, or inheritance.") String location,
            @ToolArg(description = "A fully qualified class, interface, or enum name. Wildcards like '*' are supported for 'class'.") String pattern,
            @ToolArg(description = "Optional: An annotation to check for on the class declaration.") JavaReferencedCondition.Annotated annotated) {
        var details = new JavaReferencedCondition.JavaReferenced(location, pattern, annotated, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "Builds a condition for Java member declarations. Use for locations: field, method.")
    public Condition buildMemberDeclarationCondition(
            @ToolArg(description = "The location type: field or method.") String location,
            @ToolArg(description = "For 'field', a fully qualified type name. For 'method', a signature like 'public void main'.") String pattern,
            @ToolArg(description = "Optional: An annotation to check for on the member.") JavaReferencedCondition.Annotated annotated) {
        var details = new JavaReferencedCondition.JavaReferenced(location, pattern, annotated, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "Builds a condition for Java type usages. Use for locations: type, variable_declaration, return_type, or an empty string for default.")
    public Condition buildTypeUsageCondition(
            @ToolArg(description = "The location type: type, variable_declaration, return_type, or empty string.") String location,
            @ToolArg(description = "A fully qualified class name for the type.") String pattern) {
        var details = new JavaReferencedCondition.JavaReferenced(location, pattern, null, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "Builds a condition for Java method or constructor calls. Use for locations: method_call, constructor_call.")
    public Condition buildInvocationCondition(
            @ToolArg(description = "The location type: method_call or constructor_call.") String location,
            @ToolArg(description = "For 'method_call', the pattern is 'com.example.MyClass.methodName'. For 'constructor_call', it is 'com.example.MyClass'.") String pattern) {
        var details = new JavaReferencedCondition.JavaReferenced(location, pattern, null, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "Builds a condition for Java annotation usages. Use for location: annotation.")
    public Condition buildAnnotationUsageCondition(
            @ToolArg(description = "A fully qualified annotation name, e.g., 'javax.ejb.Singleton'.") String pattern,
            @ToolArg(description = "Optional: Specific annotation elements to match.") JavaReferencedCondition.Annotated annotated) {
        var details = new JavaReferencedCondition.JavaReferenced("annotation", pattern, annotated, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "Builds a condition for Java package or import statements. Use for locations: package, import.")
    public Condition buildPackageOrImportCondition(
            @ToolArg(description = "The location type: package or import.") String location,
            @ToolArg(description = "A package name pattern, e.g., 'com.example.apps' or 'java.util.*'.") String pattern) {
        var details = new JavaReferencedCondition.JavaReferenced(location, pattern, null, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "Builds a condition to find a Java dependency by name or regex.")
    public Condition buildJavaDependencyCondition(
            @ToolArg(description = "The exact name of the dependency, e.g., 'io.quarkus:quarkus-arc'.") String name,
            @ToolArg(description = "A regex to match against the dependency name.") String nameRegex,
            @ToolArg(description = "Optional: The upper version bound (inclusive).") String upperbound,
            @ToolArg(description = "Optional: The lower version bound (inclusive).") String lowerbound) {

        var details = new JavaDependencyCondition.JavaDependency(name, nameRegex, upperbound, lowerbound);
        return new JavaDependencyCondition(details);
    }
}
