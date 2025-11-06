package org.ashkan.ghaffari.ingestor.ws.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.redis.IdempotencyRepository;
import org.ashkan.ghaffari.ingestor.ws.SessionRegistry;
import org.ashkan.ghaffari.ingestor.ws.dto.IngestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Service
public class IngestWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(IngestWebSocketHandler.class);
    private static final int MAX_TEXT_SIZE = 64 * 1024;
    private static final String CLEAN_TOPIC = "text-clean";
    private static final String FLAGGED_TOPIC = "text-flagged";

    private final KafkaTemplate<String, byte[]> kafka;
    private final ObjectMapper mapper;
    private final IdempotencyRepository idempotencyRepository;
    private final SessionRegistry sessionRegistry;

    public IngestWebSocketHandler(KafkaTemplate<String, byte[]> kafka, ObjectMapper mapper,
                                  IdempotencyRepository idempotencyRepository, SessionRegistry sessionRegistry) {
        this.kafka = kafka;
        this.mapper = mapper;
        this.idempotencyRepository = idempotencyRepository;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.setTextMessageSizeLimit(MAX_TEXT_SIZE);
        log.info("WebSocket connected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Thread.ofVirtual().start(() ->
            process(session, message.getPayload()));
    }

    private void process(WebSocketSession session, String json) {
        try {
            IngestMessage incoming = mapper.readValue(json, IngestMessage.class);

            if (incoming.idempotencyId() == null || incoming.chatId() == null || incoming.senderId() == null
                || incoming.type() == null) {
                sendError(session, "Missing required fields");
                return;
            }

            if (!idempotencyRepository.tryStore(incoming.idempotencyId())) {
                log.debug("Duplicate message, rejecting processing");
                sendError(session, "Duplicate idempotency key");
                return;
            }

            sessionRegistry.registerSession(incoming.chatId(), session);

            IngestMessage enriched = new IngestMessage(
                UUID.randomUUID(),
                incoming.idempotencyId(),
                incoming.type(),
                incoming.chatId(),
                incoming.senderId(),
                incoming.timestamp(),
                incoming.payload()
            );

            byte[] serialized = mapper.writeValueAsBytes(enriched);
            kafka.send(CLEAN_TOPIC, enriched.chatId(), serialized)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.warn("Kafka send failed: {}", ex.toString());
                    }
                });

        } catch (Exception e) {
            log.debug("Failed to process message for sessionId: {}: {}", session.getId(), e.toString());
            sendError(session, "MALFORMED_PAYLOAD");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.removeSession(session);
        log.info("WebSocket closed: {} ({})", session.getId(), status);
    }

    private void sendError(WebSocketSession session, String reason) {
        try {
            session.sendMessage(new TextMessage("{\"error\":\"" + reason + "\"}"));
        } catch (Exception ignored) {}
    }
}
