package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BuiltinRuleTools {

    @Tool(description = "Builds a condition that finds files with names matching a pattern.")
    public Condition buildFileCondition(@ToolArg(description = "Regex pattern to match file names.") String pattern) {
        BuiltinFileCondition.BuiltinFileDetails details = new BuiltinFileCondition.BuiltinFileDetails(pattern);
        return new BuiltinFileCondition(details);
    }

    @Tool(description = "Builds a condition that finds content within files.")
    public Condition buildFileContentCondition(
            @ToolArg(description = "Regex pattern to match file names.") String filePattern,
            @ToolArg(description = "Regex pattern to match within the file content.") String pattern) {
        BuiltinFileContentCondition.BuiltinFileContentDetails details = 
            new BuiltinFileContentCondition.BuiltinFileContentDetails(filePattern, pattern);
        return new BuiltinFileContentCondition(details);
    }

    @Tool(description = "Builds a condition that queries XML files using an XPath expression.")
    public Condition buildBuiltinXmlCondition(
            @ToolArg(description = "The XPath expression to execute.") String xpath,
            @ToolArg(description = "Optional: A list of file paths to search within.") List<String> filepaths,
            @ToolArg(description = "Optional: A map of namespace prefixes to URIs.") Map<String, String> namespaces) {
        BuiltinXmlCondition.BuiltinXmlDetails details = 
            new BuiltinXmlCondition.BuiltinXmlDetails(filepaths, namespaces, xpath);
        return new BuiltinXmlCondition(details);
    }

    @Tool(description = "Builds a condition that checks the public ID of an XML file.")
    public Condition buildBuiltinXmlPublicIdCondition(
            @ToolArg(description = "Regex to match against the public ID.") String regex,
            @ToolArg(description = "Optional: A list of file paths to search within.") List<String> filepaths,
            @ToolArg(description = "Optional: A map of namespace prefixes to URIs.") Map<String, String> namespaces) {
        BuiltinXmlPublicIdCondition.BuiltinXmlPublicIdDetails details = 
            new BuiltinXmlPublicIdCondition.BuiltinXmlPublicIdDetails(filepaths, namespaces, regex);
        return new BuiltinXmlPublicIdCondition(details);
    }

    @Tool(description = "Builds a condition that queries JSON files using an XPath-like expression.")
    public Condition buildBuiltinJsonCondition(
            @ToolArg(description = "The XPath-like query to execute.") String xpath,
            @ToolArg(description = "Optional: A list of file paths to search within.") List<String> filepaths) {
        BuiltinJsonCondition.BuiltinJsonDetails details = 
            new BuiltinJsonCondition.BuiltinJsonDetails(filepaths, xpath);
        return new BuiltinJsonCondition(details);
    }

    @Tool(description = "Builds a condition that checks if a set of tags are present.")
    public Condition buildBuiltinHasTagsCondition(
            @ToolArg(description = "A list of tags to check for.") List<String> tags) {
        return new BuiltinHasTagsCondition(tags);
    }
}
