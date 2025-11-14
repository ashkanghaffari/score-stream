package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.ws.dto.ChatTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

public class ChatMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageConsumer.class);
    private static final String CLEAN_TOPIC = "text-clean";
    private static final String CHAT_MESSAGE_CONSUMER_GROUP_ID = "chat-message-consumer";

    private final KafkaTemplate<String, byte[]> kafka;
    private final ObjectMapper mapper;
    private final ChatMessageService chatMessageService;

    public ChatMessageConsumer(KafkaTemplate<String, byte[]> kafka, ObjectMapper mapper,
                               ChatMessageService chatMessageService) {
        this.kafka = kafka;
        this.mapper = mapper;
        this.chatMessageService = chatMessageService;
    }

    @KafkaListener(topics = CLEAN_TOPIC, groupId = CHAT_MESSAGE_CONSUMER_GROUP_ID)
    public void onMessage(byte[] data) throws IOException {
        ChatTextMessage chatTextMessage = mapper.readValue(data, ChatTextMessage.class);
        chatMessageService.saveMessage(chatTextMessage);
    }
}
