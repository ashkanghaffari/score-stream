package org.ashkan.ghaffari.ingestor.sanitation;

import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.Transliterator;

public class NormalizationUtil {

    private static final Normalizer2 NFKC = Normalizer2.getNFKCInstance();
    private static final Transliterator DIACRITIC_STRIPPER =
        Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC");

    public static String normalize(String raw) {
        if (raw == null) return "";

        // 1. Unicode normalize (NFKC)
        String text = NFKC.normalize(raw);

        // 2. Remove diacritics (e.g., “fréé” → “free”)
        text = DIACRITIC_STRIPPER.transform(text);

        // 3. To lowercase
        text = text.toLowerCase();

        // 4. Collapse multiple spaces
        text = text.replaceAll("\\s+", " ").trim();

        // 5. Optional: collapse repeated characters (heyyy→hey)
        text = text.replaceAll("(.)\\1{2,}", "$1$1");

        return text;
    }
}
