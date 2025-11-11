package org.ashkan.ghaffari.ingestor.ruleengine;

import java.util.List;

public record ProcessedTextResult(
    String normalizedText,
    List<String> tokenizedText
) {}
