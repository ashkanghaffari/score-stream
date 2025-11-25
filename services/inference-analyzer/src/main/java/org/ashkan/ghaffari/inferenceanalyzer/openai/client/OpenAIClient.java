package org.ashkan.ghaffari.inferenceanalyzer.openai.client;

import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.ChatCompletionRequest;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.OpenAIResponse;

public interface OpenAIClient {

    OpenAIResponse complete(ChatCompletionRequest request);
}
