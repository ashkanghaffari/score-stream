package org.ashkan.ghaffari.inferenceanalyzer.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.OpenAIChatMessage;

import java.util.List;

public record OpenAIResponse(
    String id,
    String model,
    List<Choice> choices
) {
    public record Choice(
        OpenAIChatMessage message,
        @JsonProperty("finish_reason") String finishReason,
        int index
    ) {}
}
