package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.ws.dto.ChatTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageConsumer.class);
    private static final String CLEAN_TOPIC = "text-clean";
    private static final String CHAT_MESSAGE_CONSUMER_GROUP_ID = "chat-message-consumer";

    private final ObjectMapper mapper;
    private final ChatMessageService chatMessageService;

    public ChatMessageConsumer(ObjectMapper mapper, ChatMessageService chatMessageService) {
        this.mapper = mapper;
        this.chatMessageService = chatMessageService;
    }

    @KafkaListener(topics = CLEAN_TOPIC, groupId = CHAT_MESSAGE_CONSUMER_GROUP_ID)
    public void onMessage(byte[] data) throws IOException {
        ChatTextMessage chatTextMessage = mapper.readValue(data, ChatTextMessage.class);
        chatMessageService.saveMessage(chatTextMessage);
    }
}
