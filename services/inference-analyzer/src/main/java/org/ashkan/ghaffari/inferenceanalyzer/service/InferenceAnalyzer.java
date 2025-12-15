package org.ashkan.ghaffari.inferenceanalyzer.service;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;
import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.chatmessage.ChatMessageService;
import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.flaggedmessage.FlaggedMessageService;
import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.fraudanalysis.FraudAnalysisService;
import org.ashkan.ghaffari.inferenceanalyzer.model.ChatTurn;
import org.ashkan.ghaffari.inferenceanalyzer.model.ConversationContext;
import org.ashkan.ghaffari.inferenceanalyzer.openai.OpenAIService;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.FraudAnalysisResult;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InferenceAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(InferenceAnalyzer.class);

    private final FlaggedMessageService flaggedMessageService;
    private final ChatMessageService chatMessageService;
    private final OpenAIService openAIService;
    private final FraudAnalysisService fraudAnalysisService;

    public InferenceAnalyzer(FlaggedMessageService flaggedMessageService,
                             ChatMessageService chatMessageService,
                             OpenAIService openAIService, FraudAnalysisService fraudAnalysisService) {
        this.flaggedMessageService = flaggedMessageService;
        this.chatMessageService = chatMessageService;
        this.openAIService = openAIService;
        this.fraudAnalysisService = fraudAnalysisService;
    }

    public void analyze() {
        List<FlaggedMessage> flaggedMessages = flaggedMessageService.flaggedMessages();
        if (flaggedMessages.isEmpty()) {
            log.info("No flagged messages to analyze.");
            return;
        }

        List<ConversationContext> conversationContexts = flaggedMessages.stream()
            .map(this::buildConversationContext)
            .toList();
        log.info("Prepared {} conversation contexts for analysis.", conversationContexts.size());

        Map<String, FlaggedMessage> analysisIdToFlaggedMessage = conversationContexts.stream()
            .collect(Collectors.toMap(
               ConversationContext::analysisId,
               ConversationContext::flaggedMessage
            ));

        List<FraudAnalysisResult> fraudAnalysisResults = openAIService.sendAndParse(conversationContexts);
        log.info("Received {} fraud analysis results.", fraudAnalysisResults.size());
        for (FraudAnalysisResult result : fraudAnalysisResults) {
            FlaggedMessage flaggedMessage = analysisIdToFlaggedMessage.get(result.analysisId());
            if (flaggedMessage == null) {
                log.warn("Analysis result {} did not match any flagged message.", result.analysisId());
                return;
            }
            flaggedMessageService.updateWithAnalysis(flaggedMessage,
                result.analysisId());

            fraudAnalysisService.saveFraudAnalysis(result, flaggedMessage);
            log.info("Updated flagged message {} with analysis {}.", flaggedMessage.getMessageId(), result.analysisId());
        }
    }

    private ConversationContext buildConversationContext(FlaggedMessage flaggedMessage) {
        String chatScopeKey = flaggedMessage.getChatScopeKey();
        List<ChatTurn> turns = chatMessageService.getWindow(
            chatScopeKey,
            flaggedMessage.getTimestamp(),
            flaggedMessage
        ).stream()
            .map(chatMessage -> new ChatTurn(
                chatMessage.getSenderId(),
                chatMessage.getTimestamp(),
                chatMessage.getMessageId(),
                extractText(chatMessage.getPayload())
            ))
            .toList();

        return new ConversationContext(
            flaggedMessage,
            turns,
            flaggedMessage.getChatId(),
            flaggedMessage.getTimestamp(),
            UUID.randomUUID().toString()
        );
    }

    private String extractText(Map<String, Object> payload) {
        if (payload == null) {
            return "";
        }
        Object value = payload.get("text");
        return value != null ? value.toString() : "";
    }
}
