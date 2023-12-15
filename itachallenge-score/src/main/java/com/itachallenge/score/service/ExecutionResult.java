package com.itachallenge.score.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

public class ExecutionResult {
    private final boolean success;
    private final JsonNode output;

    public ExecutionResult(boolean success, String output) {
        this.success = success;
        this.output = parseOutput(output);
    }

    public boolean hasErrors() {
        return !success;
    }

    public JsonNode getOutput() {
        return output;
    }

    private JsonNode parseOutput(String output) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(output);
        } catch (Exception e) {
            e.printStackTrace();
            return new TextNode(output);
        }
    }
}
