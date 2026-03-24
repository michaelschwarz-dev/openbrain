package dev.michaelschwarz.nodebrain.llm;

import com.fasterxml.jackson.databind.JsonNode;

public record OllamaResult(String content, JsonNode toolCalls) {

    public boolean hasToolCalls() {
        return toolCalls != null && toolCalls.isArray() && !toolCalls.isEmpty();
    }
}