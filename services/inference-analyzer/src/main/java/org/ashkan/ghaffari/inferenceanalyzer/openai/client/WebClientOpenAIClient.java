package org.ashkan.ghaffari.inferenceanalyzer.openai.client;

import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.ChatCompletionRequest;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.response.OpenAIResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class WebClientOpenAIClient implements OpenAIClient {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final WebClient webClient;

    public WebClientOpenAIClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public OpenAIResponse complete(ChatCompletionRequest request) {
        return webClient.post()
            .uri(CHAT_COMPLETIONS_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(OpenAIResponse.class)
            .block(Duration.ofSeconds(30));
    }
}
