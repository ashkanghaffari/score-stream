package org.ashkan.ghaffari.ingestor.ruleengine.sanitation;

import org.apache.lucene.analysis.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextProcessor {

    private static final Logger log = LoggerFactory.getLogger(TextProcessor.class);
    private final Analyzer analyzer;

    public TextProcessor(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public ProcessedTextResult process(String raw) {
        try {
            String normalized = NormalizationUtil.normalize(raw);
            List<String> tokenized = TokenizerUtil.tokenize(analyzer, normalized);
            return new ProcessedTextResult(normalized, tokenized);
        } catch(Exception ex) {
            log.error("Failed to process text: {}", raw);
            throw new RuntimeException("Text processing failed", ex);
        }
    }

}
