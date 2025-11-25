package org.ashkan.ghaffari.inferenceanalyzer;

public record ChatTurn(
    String senderId,
    long timestamp,
    String messageId,
    String text
) {}
