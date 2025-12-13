package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.dynamo.chatmessage.ChatMessage;
import org.ashkan.ghaffari.common.ws.dto.ChatTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatMessageService {
    private static final Logger log = LoggerFactory.getLogger(ChatMessageService.class);
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper mapper;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ObjectMapper mapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.mapper = mapper;
    }

    public void saveMessage(ChatTextMessage chatTextMessage) {
        Map<String, Object> payloadMap =
            mapper.convertValue(chatTextMessage.payload(), new TypeReference<>() {});

        try {
            String scopeKey = buildScopeKey(chatTextMessage.tenantId(), chatTextMessage.appId(), chatTextMessage.chatId());
            chatMessageRepository.save(
                new ChatMessage(
                    chatTextMessage.tenantId(),
                    chatTextMessage.appId(),
                    chatTextMessage.chatId(),
                    scopeKey,
                    chatTextMessage.timestamp().toEpochMilli(),
                    chatTextMessage.id().toString(),
                    chatTextMessage.senderId(),
                    payloadMap
                )
            );
        } catch (UnsupportedOperationException ex) {
            log.error("Failed to save message to DynamoDB: {}", ex.getMessage());
        }
    }

    private String buildScopeKey(String tenantId, String appId, String chatId) {
        return tenantId + ":" + appId + ":" + chatId;
    }


}
