package org.ashkan.ghaffari.inferenceanalyzer.openai.dto;

public record OpenAIChatMessage(
    String role,
    String content
) {}
