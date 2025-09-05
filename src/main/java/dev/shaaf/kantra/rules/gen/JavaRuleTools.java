package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Java-specific tools for creating conditions that match Java code elements.
 * 
 * Supported Java Elements:
 * - Dependencies (Maven/Gradle artifacts)
 * - Code references (imports, classes, methods, fields)
 * - Annotations and annotation elements
 * 
 * Common Location Types:
 * - IMPORT: Java import statements
 * - CLASS: Class declarations  
 * - METHOD_CALL: Method invocations
 * - FIELD: Field declarations/usage
 * - ANNOTATION: Annotation usage
 * 
 * Cross-References:
 * - Combine with CoreRuleTools.combineWithAnd() for complex conditions
 * - Use with BuiltinRuleTools.buildFileCondition() to scope to specific files
 * - See KantraRuleToolsFacade.buildJavaMigrationRule() for quick Java rules
 */
@ApplicationScoped
public class JavaRuleTools {

    @Tool(description = "[JAVA] Builds a condition to find a Java dependency.")
    public Condition buildJavaDependency(
            @ToolArg(description = "The exact name of the dependency.") String name,
            @ToolArg(description = "A regex to match against the dependency name.") String nameRegex,
            @ToolArg(description = "Optional: The upper version bound (inclusive).") String upperbound,
            @ToolArg(description = "Optional: The lower version bound (inclusive).") String lowerbound) {

        JavaDependencyCondition.JavaDependencyDetails details = 
            new JavaDependencyCondition.JavaDependencyDetails(name, nameRegex, upperbound, lowerbound);
        return new JavaDependencyCondition(details);
    }

    @Tool(description = "[JAVA] Use for most Java code references. Builds a condition to find a Java element (like a class, method, or annotation usage) based on its location and pattern.")
    public Condition buildJavaReferenced(
            @ToolArg(description = "The location type, e.g., 'import', 'class', 'method_call'.") String location,
            @ToolArg(description = "The pattern to match against the Java element.") String pattern) {
        
        JavaReferencedCondition.JavaReferencedDetails details = 
            new JavaReferencedCondition.JavaReferencedDetails(pattern, location, null, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "[JAVA] Use this ONLY when you need to find a Java element that is ALSO annotated with another specific annotation. For example, to find a FIELD of a certain type that has the '@Inject' annotation.")
    public Condition buildJavaReferencedWithAnnotation(
            @ToolArg(description = "The location type, e.g., 'import', 'class', 'method_call'.") String location,
            @ToolArg(description = "The pattern to match against the Java element.") String pattern,
            @ToolArg(description = "The annotation to check for on the reference.") JavaReferencedCondition.Annotated annotated) {
        
        JavaReferencedCondition.JavaReferencedDetails details = 
            new JavaReferencedCondition.JavaReferencedDetails(pattern, location, annotated, null);
        return new JavaReferencedCondition(details);
    }

    @Tool(description = "[JAVA] Creates an annotation specification for use with buildJavaReferencedWithAnnotation.")
    public JavaReferencedCondition.Annotated createAnnotation(
            @ToolArg(description = "The annotation pattern to match.") String pattern,
            @ToolArg(description = "Optional list of annotation elements to match.") List<JavaReferencedCondition.Element> elements) {
        return new JavaReferencedCondition.Annotated(pattern, elements);
    }

    @Tool(description = "[JAVA] Creates an annotation element for use with createAnnotation.")
    public JavaReferencedCondition.Element createAnnotationElement(
            @ToolArg(description = "The element name.") String name,
            @ToolArg(description = "The element value (can be a regex pattern).") String value) {
        return new JavaReferencedCondition.Element(name, value);
    }
}