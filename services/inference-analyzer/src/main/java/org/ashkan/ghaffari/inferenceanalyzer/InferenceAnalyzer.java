package org.ashkan.ghaffari.inferenceanalyzer;

import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.chatmessage.ChatMessageService;
import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.flaggedmessage.FlaggedMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InferenceAnalyzer {

    private final FlaggedMessageService flaggedMessageService;
    private final ChatMessageService chatMessageService;

    public InferenceAnalyzer(FlaggedMessageService flaggedMessageService, ChatMessageService chatMessageService) {
        this.flaggedMessageService = flaggedMessageService;
        this.chatMessageService = chatMessageService;
    }

    public void analyze() {
        List<Flagee>
    }
}
