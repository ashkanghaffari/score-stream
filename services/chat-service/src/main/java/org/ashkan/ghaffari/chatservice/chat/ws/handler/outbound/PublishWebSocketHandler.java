package org.ashkan.ghaffari.chatservice.chat.handler.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.ws.dto.ChatTextMessage;
import org.ashkan.ghaffari.chatservice.security.interceptor.ChatIdHandshakeInterceptor;
import org.ashkan.ghaffari.chatservice.chat.SessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Service
public class PublishWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(PublishWebSocketHandler.class);
    private static final String RAW_TOPIC = "text-raw";
    private static final String WS_CONSUMER_GROUP_ID = "ws-publisher";

    private final SessionRegistry sessionRegistry;
    private final ObjectMapper mapper;

    public PublishWebSocketHandler(SessionRegistry sessionRegistry, ObjectMapper mapper) {
        this.sessionRegistry = sessionRegistry;
        this.mapper = mapper;
    }

    @KafkaListener(topics = RAW_TOPIC, groupId = WS_CONSUMER_GROUP_ID)
    public void onMessage(byte[] data) throws IOException {
        ChatTextMessage message = mapper.readValue(data, ChatTextMessage.class);
        fanOutMessage(message);
    }

    private void fanOutMessage(ChatTextMessage message) throws IOException {
        String scopeKey = ChatIdHandshakeInterceptor.buildScopeKey(
            message.tenantId(), message.appId(), message.chatId());
        Set<WebSocketSession> sessions = sessionRegistry.all(scopeKey);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message.toString()));
            } catch(IOException ex) {
                log.warn("Failed to send message id: {} {}", message.id(), ex.getMessage());
                throw ex;
            }
        }
    }
}
