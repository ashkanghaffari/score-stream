package org.ashkan.ghaffari.inferenceanalyzer.openai;

public record OpenAIChatMessage(
    String role,
    String content
) {}
