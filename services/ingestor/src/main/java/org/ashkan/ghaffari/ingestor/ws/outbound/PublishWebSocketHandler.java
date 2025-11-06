package org.ashkan.ghaffari.ingestor.ws.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.ws.SessionRegistry;
import org.ashkan.ghaffari.ingestor.ws.dto.IngestMessage;
import org.ashkan.ghaffari.ingestor.ws.dto.PublishMessage;
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
    private final SessionRegistry sessionRegistry;
    private final ObjectMapper mapper;

    public PublishWebSocketHandler(SessionRegistry sessionRegistry, ObjectMapper mapper) {
        this.sessionRegistry = sessionRegistry;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "text-clean", groupId = "ws-publisher")
    public void onMessage(byte[] data) throws IOException {
        IngestMessage message = mapper.readValue(data, IngestMessage.class);
        PublishMessage publishMessage = new PublishMessage(
            message.id(),
            message.idempotencyId(),
            message.type(),
            message.chatId(),
            message.userId(),
            message.timestamp(),
            message.payload()
        );

        fanOutMessage(publishMessage);
    }

    private void fanOutMessage(PublishMessage message) throws IOException {
        Set<WebSocketSession> recipients = sessionRegistry.all(message.chatId());
        for (WebSocketSession session : recipients) {

            try {
                session.sendMessage(new TextMessage(message.toString()));
            } catch(IOException ex) {
                log.debug("Failed to send message id: {} {}", message.id(), ex.getMessage());
                throw ex;
            }
        }
    }
}
