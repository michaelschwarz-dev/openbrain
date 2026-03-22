package dev.michaelschwarz.openbrain.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Fabrik-Klasse zur Erstellung von Tool-Definitionen.
 * Trennt die JSON-Schemagenerierung von der restlichen Logik.
 */
@ApplicationScoped
public class ToolDefinitionFactory {

    private final ObjectMapper mapper;

    @Inject
    public ToolDefinitionFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ArrayNode createRunTerminalTool() {
        var tool = mapper.createObjectNode();
        tool.put("type", "function");
        tool.set("function", buildFunctionNode());

        var tools = mapper.createArrayNode();
        tools.add(tool);
        return tools;
    }

    private ObjectNode buildFunctionNode() {
        var function = mapper.createObjectNode();
        function.put("name", "run_terminal");
        function.put("description", "Führt einen Kommandozeilen-Befehl im Terminal des Betriebssystems aus.");
        function.set("parameters", buildParametersNode());
        return function;
    }

    private ObjectNode buildParametersNode() {
        var commandProperty = mapper.createObjectNode();
        commandProperty.put("type", "string");
        commandProperty.put("description", "Der auszuführende Befehl (z.B. 'dir' oder 'ls -la')");

        var properties = mapper.createObjectNode();
        properties.set("command", commandProperty);

        var required = mapper.createArrayNode();
        required.add("command");

        var parameters = mapper.createObjectNode();
        parameters.put("type", "object");
        parameters.set("properties", properties);
        parameters.set("required", required);
        return parameters;
    }
}