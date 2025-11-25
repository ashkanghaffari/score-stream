package org.ashkan.ghaffari.inferenceanalyzer.openai.client;

import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.ChatCompletionRequest;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.response.OpenAIResponse;

public interface OpenAIClient {

    OpenAIResponse complete(ChatCompletionRequest request);
}
