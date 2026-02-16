package org.ashkan.ghaffari.ingestiongateway.apikeyvalidationclient;

import org.ashkan.ghaffari.common.validateinternalapikey.request.ValidateInternalApiKeyRequest;
import org.ashkan.ghaffari.common.validateinternalapikey.response.ValidateInternalApiKeyResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ApiKeyValidationClient {
    private static final String API_KEY_VALIDATE_PATH = "/v1/integration/api-keys/validate";
    private WebClient webClient;

    public ApiKeyValidationClient(WebClient adminConsoleWebClient) {
        this.webClient = adminConsoleWebClient;
    }

    public ValidateInternalApiKeyResponse validateInternalApiKey(ValidateInternalApiKeyRequest request) {

        int maxAttempts = 3;
        long baseDelayMillis = 200;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return webClient.post()
                    .uri(API_KEY_VALIDATE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ValidateInternalApiKeyResponse.class)
                    .block(Duration.ofMillis(500));

            } catch (Exception ex) {

                // Fail fast if not retryable
                if (!isRetryable(ex) || attempt == maxAttempts - 1) {
                    throw new RuntimeException("Admin Console API key validation request failed after retries", ex);
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

            // Retry: 500-599
            return code == 429 || (code >= 500 && code <= 599);
        }

        // Retry for IO/network errors
        return ex instanceof java.io.IOException || ex.getCause() instanceof java.io.IOException;
    }
}
