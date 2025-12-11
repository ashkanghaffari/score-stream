package org.ashkan.ghaffari.inferenceanalyzer.openai.client;

import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.ChatCompletionRequest;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.response.OpenAIResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class WebClientOpenAIClient implements OpenAIClient {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final WebClient webClient;

    public WebClientOpenAIClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public OpenAIResponse complete(ChatCompletionRequest request) {

        int maxAttempts = 3;
        long baseDelayMillis = 200;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return webClient.post()
                    .uri(CHAT_COMPLETIONS_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block(Duration.ofSeconds(30));

            } catch (Exception ex) {

                // Fail fast if not retryable
                if (!isRetryable(ex) || attempt == maxAttempts - 1) {
                    throw new RuntimeException("OpenAI request failed after retries", ex);
                }

                // Exponential backoff
                long baseDelay = baseDelayMillis * (1L << attempt);
                double jitterFactor = ThreadLocalRandom.current().nextDouble(0.8, 1.2);
                long delay = (long) (baseDelay * jitterFactor);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new IllegalStateException("Unexpected retry loop exit");
    }

    private boolean isRetryable(Exception ex) {
        if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException e) {
            int code = e.getStatusCode().value();

            // Retry: 429 (rate limit), 500-599
            return code == 429 || (code >= 500 && code <= 599);
        }

        // Retry for IO/network errors
        return ex instanceof java.io.IOException || ex.getCause() instanceof java.io.IOException;
    }

}
