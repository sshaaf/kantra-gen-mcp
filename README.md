[![Build](https://github.com/sshaaf/scribe/actions/workflows/build.yml/badge.svg)](https://github.com/sshaaf/scribe/actions/workflows/build.yml)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Maven 3](https://img.shields.io/badge/Maven-3-green.svg)](https://maven.apache.org/)
[![Quarkus 3](https://img.shields.io/badge/Quarkus-3-blue.svg)](https://quarkus.io/)


# Scribe - An MCP Server for generating Konveyor rules and more.

A Model Context Protocol (MCP) server for generating rules for Konveyor.

Examples 

## Running the Server

### Using Native Binary
```bash
./scribe-linux-x64.bin
# or on macOS
./scribe-macos-arm64.bin
```

### Using Uber JAR
```bash
java -jar scribe.jar
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
    "scribe": {
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
    "scribe": {
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

### Goose CLI
Add to your Goose config:
```yaml
extensions:
  scribe:
    display_name: Scribe MCP
    enabled: true
    name: scribe
    timeout: 300
    type: sse
    uri: "http://localhost:8080/mcp/sse"
```

