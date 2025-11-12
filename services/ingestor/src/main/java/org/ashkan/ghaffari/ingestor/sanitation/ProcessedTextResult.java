package org.ashkan.ghaffari.ingestor.sanitation;

import java.util.List;

public record ProcessedTextResult(
    String normalizedText,
    List<String> tokenizedText
) {}
