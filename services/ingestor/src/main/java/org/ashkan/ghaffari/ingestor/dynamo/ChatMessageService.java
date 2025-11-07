package org.ashkan.ghaffari.ingestor.dynamo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.ws.dto.ChatTextMessage;
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
            chatMessageRepository.save(
                new ChatMessage(
                    chatTextMessage.chatId(),
                    chatTextMessage.timestamp().toEpochMilli(),
                    chatTextMessage.id().toString(),
                    chatTextMessage.senderId(),
                    payloadMap,
                    false
                )
            );
        } catch (UnsupportedOperationException ex) {
            log.error("Failed to save message to DynamoDB: {}", ex.getMessage());
        }
    }


}
