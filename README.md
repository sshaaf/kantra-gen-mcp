[![Build](https://github.com/sshaaf/kantra-gen-mcp/actions/workflows/build.yml/badge.svg)](https://github.com/sshaaf/kantra-gen-mcp/actions/workflows/build.yml)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Maven 3](https://img.shields.io/badge/Maven-3-green.svg)](https://maven.apache.org/)
[![Quarkus 3](https://img.shields.io/badge/Quarkus-3-blue.svg)](https://quarkus.io/)


# An MCP Server for Konveyor Kantra to generate rules and more.

A Model Context Protocol (MCP) server for Kantra rule generation.

Examples 

## Running the Server

### Using Native Binary
```bash
./kantra-rules-gen-linux-x64.bin
# or on macOS
./kantra-rules-gen-macos-arm64.bin
```

### Using Uber JAR
```bash
java -jar kantra-rules-gen.jar
```

### Using Quarkus Dev Mode
```bash
mvn quarkus:dev
```

The server runs on `http://localhost:8080` by default.

## MCP Client Configuration

### Cursor / VS Code
Add to your `mcp.json`:
```json
{
  "mcpServers": {
    "kantra-gen-mcp": {
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

### Claude Desktop
Add to `claude_desktop_config.json`:
```json
{
  "mcpServers": {
    "kantra-gen-mcp": {
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

### Goose CLI
Add to your Goose config:
```yaml
extensions:
  kantra_rules_gen:
    display_name: Kantra Rules Gen MCP
    enabled: true
    name: kantra-gen-mcp
    timeout: 300
    type: sse
    uri: "http://localhost:8080/mcp/sse"
```

## Example Rules
See [test/rules](test/rules) for example migration rules including:
- EJB to Quarkus
- Monolith to Microservices  
- Struts to Spring Boot

