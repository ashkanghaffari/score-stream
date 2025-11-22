package org.ashkan.ghaffari.inferenceanalyzer.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatCompletionRequest(
    String model,
    List<OpenAIChatMessage> messages,
    double temperature,
    @JsonProperty("max_tokens") int maxTokens,
    @JsonProperty("response_format") ResponseFormat responseFormat
) {
    public record ResponseFormat(String type) {}
}
