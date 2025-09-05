[![Build and Push Backend](https://github.com/sshaaf/kantra-gen-mcp/actions/workflows/build-and-push.yml/badge.svg)](https://github.com/sshaaf/kantra-gen-mcp/actions/workflows/build-and-push.yml)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Maven 3](https://img.shields.io/badge/Maven-3-green.svg)](https://maven.apache.org/)
[![Quarkus 3](https://img.shields.io/badge/Quarkus-3-blue.svg)](https://quarkus.io/)
[![semantic-release: angular](https://img.shields.io/badge/semantic--release-angular-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

# An MCP Server for Konveyor Kantra to generate rules and more.

A Model Context Protocol (MCP) server for Kantra rule generation.

## 🚀 Why This Project!

This MCP server revolutionizes how you generate Kantra rules by bringing the power of AI directly into your development workflow. Here's why it's a game-changer:

### ✨ **Seamless AI Integration**
- **No more context switching**: Generate rules directly from your IDE without leaving your development environment
- **Intelligent rule generation**: Leverage AI models to create sophisticated, context-aware rules
- **Natural language interface**: Describe what you want in plain English, get production-ready rules

### 🎯 **Supercharged Productivity**
- **Instant rule creation**: What used to take hours of manual configuration now happens in seconds
- **Consistent rule patterns**: AI ensures your rules follow best practices and maintain consistency
- **Rapid prototyping**: Test different rule approaches quickly without manual setup

### 🔧 **Developer Experience**
- **IDE-native experience**: Works seamlessly with Cursor, VS Code, Claude Desktop, and more
- **Real-time feedback**: Get immediate suggestions and improvements for your rules
- **Version control friendly**: Generated rules are ready for your existing Git workflow

## 🏗️ Conceptual Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   MCP Client    │    │   MCP Server     │    │   AI Model      │
│   (Your IDE)    │◄──►│   (This Project) │◄──►│   (Claude/GPT)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  User Request   │    │  Rule Generation │    │  AI Processing  │
│  "Create a rule │    │  Engine          │    │  & Analysis     │
│   for Java 8    │    │  - Builtin       │    │  - Context      │
│   migration"    │    │  - Java          │    │  - Patterns     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### 🔄 **How It Works**

1. **User Interaction**: You ask for a rule in your IDE using natural language
2. **MCP Communication**: Your IDE sends the request to the MCP server via stdio
3. **AI Processing**: The server analyzes your request using AI models
4. **Rule Generation**: Sophisticated rule generation engine creates the appropriate Kantra rules
5. **Response**: Generated rules are returned to your IDE, ready to use

### 🛠️ **Available Tools**

This MCP server helps create and manage rules for Kantra, a tool for modernizing Java applications.

*   `combineWithAnd`: Combine multiple conditions with AND logic.
*   `combineWithOr`: Combine multiple conditions with OR logic.
*   `createFileContentRule`: Create a rule to detect specific content within files.
*   `createJavaClassRule`: Create a rule to detect the usage of specific Java classes or methods.
*   `createJavaImportRule`: Create a rule to detect specific Java import statements.
*   `createXmlRule`: Create a rule to detect XML content using XPath expressions.
*   `getHelp`: Get help and examples for creating Kantra rules.
*   `validateRule`: Validate a YAML rule file for syntax and schema compliance.

## 🎯 **Real-World Benefits**

### **For Migration Projects**
- **Accelerated timelines**: Generate hundreds of rules in minutes, not days
- **Reduced errors**: AI-generated rules follow proven patterns and best practices
- **Comprehensive coverage**: Ensure no migration scenarios are missed

### **For Development Teams**
- **Knowledge democratization**: developers can create sophisticated rules
- **Consistent quality**: AI ensures all rules meet the same high standards
- **Rapid iteration**: Quickly adapt rules based on project feedback

### **For Organizations**
- **Cost reduction**: Dramatically reduce manual rule creation time
- **Risk mitigation**: AI-validated rules reduce migration risks
- **Scalability**: Handle large-scale migrations with confidence

## Getting started - configuration

### Cursor
You can add the following in the config in the `~/.cursor/mcp.json`
```yaml
{
  "mcpServers": {
    "kantra_rules_gen": {
      "type": "stdio",
      "command": "<full path> kantra-rules-gen-1.0.0-SNAPSHOT-runner",
      "args": []
    }
  }
}

```


You can add the keycloak server by adding the following to `claude_desktop_config`.
### Claude Code
```yaml
{
  "mcpServers": {
    "keycloak": {
      "command": "<full path>/kantra-rules-gen-1.0.0-SNAPSHOT-runner",
      "args": []
    }
  }
}
```
### VSCode
You can add the keycloak MCP server tools into VS Code by adding the following to your `mcp.json`.

```yaml
  "kantra_rules_gen": {
    "type": "stdio",
    "command": "<full path> kantra-rules-gen-1.0.0-SNAPSHOT-runner",
    "args": [],

```

### Goose CLI

```yaml
extensions:
  kantra_rules_gen:
    display_name: Kantra Rules Gen MCP
    enabled: true
    name: keycloak-mcp-server
    timeout: 300
    type: stdio
    cmd: "<full path>kantra-rules-gen-1.0.0-SNAPSHOT-runner"
    args: []

```

### Uber Jar
The examples above are for native binaries. however you can also use the uber-jar
If using the uber jar change the `cmd` and `args` as follows
```yaml
    cmd|command: "java"
    args: ["-jar", "path to jar"]
```

To build and use it with goose

```
mvn clean compile package -Dquarkus.package.jar.type=uber-jar
goose session --with-extension="java -jar target/kantra-gen-mcp-1.0.0-SNAPSHOT-runner.jar"
```

## Build and Release

This project uses GitHub Actions for automated builds and releases.

### Build Workflow

The `build-and-push.yml` workflow builds:
- **Uber JAR**: Complete runnable Java application
- **Native Binaries**: 
  - Linux: `kantra-gen-mcp-*-runner`
  - macOS: `kantra-gen-mcp-*-runner` 
  - Windows: `kantra-gen-mcp-*-runner.exe`

### Semantic Release for Contributors. 

The `semantic-release.yml` workflow automatically creates releases based on conventional commit messages.

#### Commit Message Format

Use conventional commit messages to trigger automatic releases:

- `feat:` - New features (triggers minor release)
- `fix:` - Bug fixes (triggers patch release)
- `BREAKING CHANGE:` - Breaking changes (triggers major release)
- `docs:` - Documentation changes (no release)
- `style:` - Code style changes (no release)
- `refactor:` - Code refactoring (no release)
- `test:` - Test changes (no release)
- `chore:` - Maintenance tasks (no release)

#### Examples

```bash
# Patch release (1.0.0 -> 1.0.1)
git commit -m "fix: resolve memory leak in rule processing"

# Minor release (1.0.0 -> 1.1.0)
git commit -m "feat: add support for custom rule templates"

# Major release (1.0.0 -> 2.0.0)
git commit -m "feat: BREAKING CHANGE: new rule engine API"
```

#### Release Process

1. Push commits with conventional messages to `main` branch
2. Build workflow runs and creates artifacts
3. Semantic release workflow analyzes commits
4. If new version is needed, creates GitHub release with:
   - Release notes from commit messages
   - CHANGELOG.md update
   - All build artifacts attached

## Development

### Prerequisites

- Java 21
- Maven 3.8+
- GraalVM (for native builds)

### Local Build

```bash
# Build JAR
mvn clean package

# Build native binary
mvn clean package -Pnative

# Run tests
mvn test
```


### Semantic Release

The semantic release is configured in:
- `package.json` - Basic configuration
- `.releaserc.json` - Detailed plugin configuration

### GitHub Actions

- `build-and-push.yml` - Main build workflow
- `semantic-release.yml` - Automated release workflow

## Artifacts

Each release includes:
- **JAR file**: `kantra-gen-mcp-{version}-jar.jar`
- **Linux binary**: `kantra-gen-mcp-{version}-linux`
- **macOS binary**: `kantra-gen-mcp-{version}-macos`
- **Windows binary**: `kantra-gen-mcp-{version}-windows.exe`

