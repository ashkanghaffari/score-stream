package org.ashkan.ghaffari.adminconsole.controller;


import jakarta.validation.Valid;
import org.ashkan.ghaffari.adminconsole.dto.request.ValidateApiKeyRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.ValidateApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.service.IntegrationApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/integration/api-keys/validate")
public class IntegrationApiKeyValidationController {
    private final IntegrationApiKeyService integrationApiKeyService;

    public IntegrationApiKeyValidationController(IntegrationApiKeyService integrationApiKeyService) {
        this.integrationApiKeyService = integrationApiKeyService;
    }

    @PostMapping
    public ResponseEntity<ValidateApiKeyResponse> validateAPIKey(@Valid @RequestBody ValidateApiKeyRequest request) {
        return ResponseEntity.ok(integrationApiKeyService.validateApiKey(request.keyHash()));
    }

}
