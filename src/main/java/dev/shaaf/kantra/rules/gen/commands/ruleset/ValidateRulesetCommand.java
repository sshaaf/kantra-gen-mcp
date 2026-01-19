package dev.shaaf.kantra.rules.gen.commands.ruleset;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.kantra.rules.gen.KantraOperation;
import dev.shaaf.kantra.rules.gen.commands.AbstractCommand;
import dev.shaaf.kantra.rules.gen.commands.RegisteredCommand;
import dev.shaaf.kantra.rules.gen.validation.RuleValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Validates a ruleset directory structure, ensuring it has:
 * - A valid ruleset.yaml file
 * - Valid rule YAML files
 * Returns a detailed validation report.
 */
@ApplicationScoped
@RegisteredCommand
public class ValidateRulesetCommand extends AbstractCommand {

    @Override
    public KantraOperation getOperation() {
        return KantraOperation.VALIDATE_RULESET;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"directoryPath"};
    }

    @Override
    public String getDescription() {
        return "Validate a ruleset directory structure. Checks for ruleset.yaml presence and validates all rule files.";
    }

    @Override
    public String getExampleParams() {
        return """
            {
                "directoryPath": "/path/to/rules/my-ruleset"
            }
            """;
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String directoryPath = requireString(params, "directoryPath");
        Path dir = Paths.get(directoryPath);
        
        ValidationReport report = new ValidationReport();
        report.directoryPath = directoryPath;
        
        // Check if directory exists
        if (!Files.exists(dir)) {
            report.valid = false;
            report.errors.add("Directory does not exist: " + directoryPath);
            return formatReport(report);
        }
        
        if (!Files.isDirectory(dir)) {
            report.valid = false;
            report.errors.add("Path is not a directory: " + directoryPath);
            return formatReport(report);
        }
        
        // Check for ruleset.yaml
        Path rulesetYaml = dir.resolve("ruleset.yaml");
        if (!Files.exists(rulesetYaml)) {
            report.hasRulesetYaml = false;
            report.errors.add("Missing ruleset.yaml file");
        } else {
            report.hasRulesetYaml = true;
            validateRulesetYaml(rulesetYaml, report);
        }
        
        // Find and validate all rule files
        try (Stream<Path> files = Files.list(dir)) {
            List<Path> ruleFiles = files
                .filter(p -> p.toString().endsWith(".yaml") || p.toString().endsWith(".yml"))
                .filter(p -> !p.getFileName().toString().equals("ruleset.yaml"))
                .sorted()
                .toList();
            
            report.totalRuleFiles = ruleFiles.size();
            
            for (Path ruleFile : ruleFiles) {
                validateRuleFile(ruleFile, report);
            }
        }
        
        // Determine overall validity
        report.valid = report.errors.isEmpty();
        
        return formatReport(report);
    }
    
    private void validateRulesetYaml(Path rulesetYaml, ValidationReport report) {
        try {
            String content = Files.readString(rulesetYaml);
            Yaml yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(content);
            
            if (parsed == null) {
                report.errors.add("ruleset.yaml is empty");
                return;
            }
            
            // Check required fields
            if (!parsed.containsKey("name") || parsed.get("name") == null) {
                report.errors.add("ruleset.yaml missing required field: name");
            } else {
                report.rulesetName = parsed.get("name").toString();
            }
            
            // Check optional but recommended fields
            if (!parsed.containsKey("description")) {
                report.warnings.add("ruleset.yaml missing recommended field: description");
            }
            
            if (!parsed.containsKey("labels")) {
                report.warnings.add("ruleset.yaml missing recommended field: labels");
            } else {
                Object labels = parsed.get("labels");
                if (labels instanceof List<?> labelList) {
                    report.labels = labelList.stream()
                        .map(Object::toString)
                        .toList();
                }
            }
            
        } catch (IOException e) {
            report.errors.add("Failed to read ruleset.yaml: " + e.getMessage());
        } catch (Exception e) {
            report.errors.add("Invalid YAML in ruleset.yaml: " + e.getMessage());
        }
    }
    
    private void validateRuleFile(Path ruleFile, ValidationReport report) {
        String fileName = ruleFile.getFileName().toString();
        
        try {
            String content = Files.readString(ruleFile);
            RuleValidator.ValidationResult result = ruleValidator.validateYamlRule(content);
            
            if (result.isValid()) {
                report.validRuleFiles.add(fileName);
            } else {
                report.invalidRuleFiles.put(fileName, result.errors());
                report.errors.add("Invalid rule file '" + fileName + "': " + result.errors());
            }
        } catch (IOException e) {
            report.errors.add("Failed to read rule file '" + fileName + "': " + e.getMessage());
            report.invalidRuleFiles.put(fileName, List.of(e.getMessage()));
        }
    }
    
    private String formatReport(ValidationReport report) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  Ruleset Validation Report\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        
        sb.append("Directory: ").append(report.directoryPath).append("\n");
        sb.append("Status: ").append(report.valid ? "✓ VALID" : "✗ INVALID").append("\n\n");
        
        // Ruleset info
        sb.append("─── Ruleset Info ───\n");
        sb.append("  Has ruleset.yaml: ").append(report.hasRulesetYaml ? "Yes" : "No").append("\n");
        if (report.rulesetName != null) {
            sb.append("  Name: ").append(report.rulesetName).append("\n");
        }
        if (report.labels != null && !report.labels.isEmpty()) {
            sb.append("  Labels: ").append(String.join(", ", report.labels)).append("\n");
        }
        sb.append("\n");
        
        // Rule files summary
        sb.append("─── Rule Files ───\n");
        sb.append("  Total: ").append(report.totalRuleFiles).append("\n");
        sb.append("  Valid: ").append(report.validRuleFiles.size()).append("\n");
        sb.append("  Invalid: ").append(report.invalidRuleFiles.size()).append("\n");
        
        if (!report.validRuleFiles.isEmpty()) {
            sb.append("\n  Valid files:\n");
            for (String file : report.validRuleFiles) {
                sb.append("    ✓ ").append(file).append("\n");
            }
        }
        
        if (!report.invalidRuleFiles.isEmpty()) {
            sb.append("\n  Invalid files:\n");
            for (Map.Entry<String, List<String>> entry : report.invalidRuleFiles.entrySet()) {
                sb.append("    ✗ ").append(entry.getKey()).append("\n");
                for (String error : entry.getValue()) {
                    sb.append("      - ").append(error).append("\n");
                }
            }
        }
        
        // Errors
        if (!report.errors.isEmpty()) {
            sb.append("\n─── Errors ───\n");
            for (String error : report.errors) {
                sb.append("  ✗ ").append(error).append("\n");
            }
        }
        
        // Warnings
        if (!report.warnings.isEmpty()) {
            sb.append("\n─── Warnings ───\n");
            for (String warning : report.warnings) {
                sb.append("  ⚠ ").append(warning).append("\n");
            }
        }
        
        sb.append("\n═══════════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    private static class ValidationReport {
        String directoryPath;
        boolean valid = true;
        boolean hasRulesetYaml = false;
        String rulesetName;
        List<String> labels;
        int totalRuleFiles = 0;
        List<String> validRuleFiles = new ArrayList<>();
        Map<String, List<String>> invalidRuleFiles = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
    }
}
