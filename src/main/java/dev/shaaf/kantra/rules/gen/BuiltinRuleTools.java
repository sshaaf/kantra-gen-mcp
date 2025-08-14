package dev.shaaf.kantra.rules.gen;


import dev.shaaf.kantra.rules.gen.builtin.model.*;
import dev.shaaf.kantra.rules.gen.core.model.Condition;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BuiltinRuleTools {

    @Tool(description = "Builds a condition that finds files with names matching a pattern.")
    public Condition buildFileCondition(@ToolArg(description = "Regex pattern to match file names.") String pattern) {
        return new BuiltinFileCondition(new BuiltinFileCondition.BuiltinFile(pattern));
    }

    @Tool(description = "Builds a condition that finds content within files.")
    public Condition buildFileContentCondition(
            @ToolArg(description = "Regex pattern to match file names.") String filePattern,
            @ToolArg(description = "Regex pattern to match within the file content.") String pattern) {
        return new BuiltinFileContentCondition(new BuiltinFileContentCondition.BuiltinFileContent(filePattern, pattern));
    }

    @Tool(description = "Builds a condition that queries XML files using an XPath expression.")
    public Condition buildBuiltinXmlCondition(
            @ToolArg(description = "The XPath expression to execute.") String xpath,
            @ToolArg(description = "Optional: A list of file paths to search within.") List<String> filepaths,
            @ToolArg(description = "Optional: A map of namespace prefixes to URIs.") Map<String, String> namespaces) {
        return new BuiltinXmlCondition(new BuiltinXmlCondition.BuiltinXml(filepaths, namespaces, xpath));
    }

    @Tool(description = "Builds a condition that checks the public ID of an XML file.")
    public Condition buildBuiltinXmlPublicIdCondition(
            @ToolArg(description = "Regex to match against the public ID.") String regex,
            @ToolArg(description = "Optional: A list of file paths to search within.") List<String> filepaths,
            @ToolArg(description = "Optional: A map of namespace prefixes to URIs.") Map<String, String> namespaces) {
        return new BuiltinXmlPublicIdCondition(new BuiltinXmlPublicIdCondition.BuiltinXmlPublicId(regex, namespaces, filepaths));
    }

    @Tool(description = "Builds a condition that queries JSON files using an XPath-like expression.")
    public Condition buildBuiltinJsonCondition(
            @ToolArg(description = "The XPath-like query to execute.") String xpath,
            @ToolArg(description = "Optional: A list of file paths to search within.") List<String> filepaths) {
        return new BuiltinJsonCondition(new BuiltinJsonCondition.BuiltinJson(xpath, filepaths));
    }

    @Tool(description = "Builds a condition that checks if a set of tags are present.")
    public Condition buildBuiltinHasTagsCondition(
            @ToolArg(description = "A list of tags to check for.") List<String> tags) {
        return new BuiltinHasTagsCondition(tags);
    }
}