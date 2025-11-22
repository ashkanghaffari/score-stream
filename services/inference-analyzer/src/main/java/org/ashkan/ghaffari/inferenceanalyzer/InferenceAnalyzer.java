package org.ashkan.ghaffari.inferenceanalyzer;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;
import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.chatmessage.ChatMessageService;
import org.ashkan.ghaffari.inferenceanalyzer.dynamodb.flaggedmessage.FlaggedMessageService;
import org.ashkan.ghaffari.inferenceanalyzer.openai.OpenAIService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InferenceAnalyzer {

    private final FlaggedMessageService flaggedMessageService;
    private final ChatMessageService chatMessageService;
    private final OpenAIService openAIService;

    public InferenceAnalyzer(FlaggedMessageService flaggedMessageService,
                             ChatMessageService chatMessageService,
                             OpenAIService openAIService) {
        this.flaggedMessageService = flaggedMessageService;
        this.chatMessageService = chatMessageService;
        this.openAIService = openAIService;
    }

    public void analyze() {
        List<FlaggedMessage> flaggedMessages = flaggedMessageService.flaggedMessages();

        List<ConversationContext> conversationContexts = flaggedMessages.stream()
            .map(this::buildConversationContext)
            .toList();

        openAIService.send(conversationContexts);
    }

    private ConversationContext buildConversationContext(FlaggedMessage flaggedMessage) {
        List<ChatTurn> turns = chatMessageService.getWindow(
                flaggedMessage.getChatId(),
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
