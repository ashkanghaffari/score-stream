package org.ashkan.ghaffari.ingestor.ruleengine.sanitation;

import java.util.List;

public record ProcessedTextResult(
    String normalizedText,
    List<String> tokenizedText
) {}
