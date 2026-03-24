# NodeBrain Terminal Agent

An AI-powered terminal agent that uses local LLMs (via Ollama) to execute terminal commands.

## Overview

NodeBrain is a Java-based chat agent built on Quarkus, Picocli, and JLine. It can execute terminal commands to solve tasks.

## Features

- **LLM Integration**: Connects to Ollama (default: `qwen3.5:4b`)
- **Terminal Execution**: Runs shell commands (Windows: `cmd.exe`, Linux/macOS: `bash`)
- **Chat Context**: Manages conversation history with system, user, assistant, and tool messages
- **Tool Support**: Uses function calling for terminal operations (`run_terminal`)
- **Streaming Responses**: Real-time output of LLM responses
- **Windows UTF-8 Support**: Automatic codepage switching on Windows

## Prerequisites

- Java 25
- Maven 3.x
- Ollama installed and running (`http://localhost:11434`)
- An LLM model (e.g., `qwen3.5:4b`)

## Installation

### 1. Clone the repository

```bash
git clone git@github.com:capgeti/nodebrain.git
cd nodebrain
```

### 2. Ensure Ollama is running

```bash
ollama serve
ollama pull qwen3.5:4b
```

### 3. Build the project

```bash
mvn clean package
# Native build (optional):
mvn clean package -Pnative
```

## Usage

### Starting the agent

```bash
java -jar target/nodebrain-0.1.0-SNAPSHOT.jar
```

### Interaction

- Prompt: `❯ `
- Exit: Type `exit` or press Ctrl+C

## Architecture

### Core Components

| Class                   | Description                                    |
|-------------------------|------------------------------------------------|
| `NodeBrainApplication`  | Main controller, entry point with Picocli      |
| `ConsoleUi`             | User interface based on JLine                  |
| `TerminalExecutor`      | Executes terminal commands (timeout: 100s)     |
| `OllamaClient`          | HTTP client for Ollama API with streaming      |
| `ChatContext`           | Manages conversation history (JSON messages)   |
| `ToolDefinitionFactory` | Creates tool definitions for function calling  |
| `ObjectMapperProducer`  | CDI producer for Jackson ObjectMapper          |

### Technologies

- **Quarkus** (3.32.3) - Framework with CDI (Contexts and Dependency Injection)
- **Picocli** - Command-line parser for CLI arguments
- **JLine** (4.0.4) - Terminal handling and line editing
- **Jackson** - JSON processing
- **Apache Commons Exec** (1.6.0) - Process execution
- **JNA** - Native Access for system integration
- **Jansi** - ANSI console support

## System Prompt

The system prompt is loaded from `src/main/resources/systemprompt.md` and initializes the chat context.

## Logging

- Console logging: disabled
- File logging: `nodebrain.log` (max. 1MB, rotation with 30 backups)

---

**Created by**: Michael Schwarz
**Version**: 0.1.0-SNAPSHOT
