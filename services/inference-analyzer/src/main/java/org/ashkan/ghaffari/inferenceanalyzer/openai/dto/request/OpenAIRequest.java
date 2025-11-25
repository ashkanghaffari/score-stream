package org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request;

import org.ashkan.ghaffari.inferenceanalyzer.ChatTurn;

import java.util.List;

public record OpenAIRequest(
    Metadata metadata,
    Trigger trigger,
    List<ChatTurn> conversation
) {}
