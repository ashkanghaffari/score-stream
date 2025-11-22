package org.ashkan.ghaffari.inferenceanalyzer.openai;

public record Metadata(
    String analysisId,
    String chatId,
    long flaggedTimestamp
) {}
