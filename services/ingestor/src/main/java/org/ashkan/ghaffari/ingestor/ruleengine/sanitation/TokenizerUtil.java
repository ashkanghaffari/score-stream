package org.ashkan.ghaffari.ingestor.ruleengine.sanitation;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.util.ArrayList;
import java.util.List;

public class TokenizerUtil {

    public static List<String> tokenize(Analyzer analyzer, String text) throws Exception {
        List<String> result = new ArrayList<>();

        try (TokenStream ts = analyzer.tokenStream("field", text)) {
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();

            while (ts.incrementToken()) {
                result.add(term.toString());
            }

            ts.end();
        }
        return result;
    }
}
