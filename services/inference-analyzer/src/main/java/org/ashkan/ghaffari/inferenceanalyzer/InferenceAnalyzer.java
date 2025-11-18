package org.ashkan.ghaffari.inferenceanalyzer;

import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.flaggedmessage.FlaggedMessageService;
import org.springframework.stereotype.Service;

@Service
public class InferenceAnalyzer {

    private final FlaggedMessageService flaggedMessageService;

    public InferenceAnalyzer(FlaggedMessageService flaggedMessageService) {
        this.flaggedMessageService = flaggedMessageService;
    }


}
