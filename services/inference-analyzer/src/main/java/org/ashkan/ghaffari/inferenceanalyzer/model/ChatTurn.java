package org.ashkan.ghaffari.inferenceanalyzer.model;

public record ChatTurn(
    String senderId,
    long timestamp,
    String messageId,
    String text
) {}
