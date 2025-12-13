package org.ashkan.ghaffari.inferenceanalyzer.dynamodb.chatmessage;

import org.ashkan.ghaffari.common.dynamo.chatmessage.ChatMessage;
import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    private static final int WINDOW_SIZE = 2;

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessage> getWindow(String chatScopeKey, long ts, FlaggedMessage flaggedMessage) {
        List<ChatMessage> before = chatMessageRepository.findBefore(chatScopeKey, ts, WINDOW_SIZE);
        List<ChatMessage> after = chatMessageRepository.findAfter(chatScopeKey, ts, WINDOW_SIZE);

        List<ChatMessage> window = new ArrayList<>();
        window.addAll(before);
        window.add(toChatMessage(flaggedMessage));
        window.addAll(after);

        return window;
    }

    private ChatMessage toChatMessage(FlaggedMessage f) {
        return new ChatMessage(
            f.getTenantId(),
            f.getAppId(),
            f.getChatId(),
            f.getChatScopeKey(),
            f.getTimestamp(),
            f.getMessageId(),
            f.getSenderId(),
            f.getPayload()
        );
    }
}
