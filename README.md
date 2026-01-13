[![Build](https://github.com/sshaaf/kantra-gen-mcp/actions/workflows/build.yml/badge.svg)](https://github.com/sshaaf/kantra-gen-mcp/actions/workflows/build.yml)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Maven 3](https://img.shields.io/badge/Maven-3-green.svg)](https://maven.apache.org/)
[![Quarkus 3](https://img.shields.io/badge/Quarkus-3-blue.svg)](https://quarkus.io/)


# An MCP Server for Konveyor Kantra to generate rules and more.

A Model Context Protocol (MCP) server for Kantra rule generation.

Examples 

## Configurations

### Claude Code
You can add the keycloak server by adding the following to `claude_desktop_config`.

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

## Using Quarkus dev mode. 
```
mvn quarkus:dev
```
configuration for mcp would look something like this
```
      "kantra-gen-mcp": {
        "url": "http://localhost:8080/mcp/sse"
      }
```

