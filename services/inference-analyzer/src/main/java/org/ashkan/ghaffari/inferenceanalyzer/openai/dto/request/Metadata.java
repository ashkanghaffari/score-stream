package org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request;

public record Metadata(
    String analysisId,
    String chatId,
    long flaggedTimestamp
) {}
