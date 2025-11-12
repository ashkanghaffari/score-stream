package org.ashkan.ghaffari.ingestor.sanitation;

import org.apache.lucene.analysis.Analyzer;

import java.util.List;

public class TextProcessor {
    private final Analyzer analyzer;

    public TextProcessor(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public ProcessedTextResult process(String raw) throws Exception {
        String normalized = NormalizationUtil.normalize(raw);
        List<String> tokenized = TokenizerUtil.tokenize(analyzer, normalized);
        return new ProcessedTextResult(normalized, tokenized);
    }

}
