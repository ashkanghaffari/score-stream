package org.ashkan.ghaffari.chatservice.handler.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.ws.dto.ChatTextMessage;
import org.ashkan.ghaffari.common.ws.dto.IngestMessage;
import org.ashkan.ghaffari.common.ws.dto.MessageType;
import org.ashkan.ghaffari.chatservice.logging.LoggingContext;
import org.ashkan.ghaffari.chatservice.redis.IdempotencyRepository;
import org.ashkan.ghaffari.chatservice.ChatIdHandshakeInterceptor;
import org.ashkan.ghaffari.chatservice.SessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
public class IngestWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(IngestWebSocketHandler.class);
    private static final int MAX_TEXT_SIZE = 64 * 1024;
    private static final String RAW_TOPIC = "text-raw";

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
        String chatScopeKey = sessionRegistry.getChatScopeKey(session);
        if (chatScopeKey == null) {
            log.warn("Closing WebSocket {} due to missing chatScopeKey attribute", session.getId());
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException e) {
                log.debug("Failed to close session {}: {}", session.getId(), e.getMessage());
            }
            return;
        }

        sessionRegistry.registerSession(chatScopeKey, session);
        log.info("WebSocket connected: {} (chatScopeKey={})", session.getId(), chatScopeKey);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Thread.ofVirtual().start(() ->
            process(session, message.getPayload()));
    }

    private void process(WebSocketSession session, String json) {
        try {
            IngestMessage incoming = mapper.readValue(json, IngestMessage.class);

            if (incoming.idempotencyId() == null || incoming.tenantId() == null || incoming.appId() == null ||
                incoming.chatId() == null || incoming.senderId() == null || incoming.type() == null) {
                sendError(session, "Missing required fields");
                return;
            }

            String incomingChatScopeKey = ChatIdHandshakeInterceptor.buildScopeKey(
                incoming.tenantId(), incoming.appId(), incoming.chatId());
            if (!incomingChatScopeKey.equals(sessionRegistry.getChatScopeKey(session))) {
                sendError(session, "Invalid chatScopeKey");
                return;
            }

            String tenantIdempotencyId = incoming.tenantId() + ":" + incoming.appId() + ":" + incoming.idempotencyId();
            if (!idempotencyRepository.tryStore(tenantIdempotencyId)) {
                log.debug("Duplicate message, rejecting processing");
                sendError(session, "Duplicate idempotency key");
                return;
            }

            if (incoming.payload() == null) {
                sendError(session, "Payload is required");
                return;
            }

            String textValue = null;
            if (incoming.type() == MessageType.TEXT) {
                textValue = extractPayloadText(incoming.payload());
                if (textValue == null || textValue.isBlank()) {
                    sendError(session, "Payload must include non-empty \"text\" for TEXT messages");
                    return;
                }
            }

        ChatTextMessage chatTextMessage = new ChatTextMessage(
            UUID.randomUUID(),
            incoming.idempotencyId(),
            incoming.type(),
            incoming.tenantId(),
            incoming.appId(),
            incoming.chatId(),
            incoming.senderId(),
            Instant.now(),
            incoming.payload()
        );

        LoggingContext.withContext(
            chatTextMessage.tenantId(),
            chatTextMessage.appId(),
            chatTextMessage.chatId(),
            chatTextMessage.id().toString(),
            chatTextMessage.idempotencyId(),
            () -> {
                try {
                    byte[] serialized = mapper.writeValueAsBytes(chatTextMessage);
                    kafka.send(RAW_TOPIC,
                        ChatIdHandshakeInterceptor.buildScopeKey(
                            chatTextMessage.tenantId(), chatTextMessage.appId(), chatTextMessage.chatId()
                        ),
                        serialized)
                        .whenComplete((res, ex) -> {
                            if (ex != null) {
                                log.warn("Kafka send failed for topic {}: {}", RAW_TOPIC, ex.toString(), ex);
                            }
                        });
                } catch (JsonProcessingException ex) {
                    throw new IllegalStateException("Failed to serialize payload for topic " + RAW_TOPIC, ex);
                }
            }
        );

        } catch (Exception e) {
            log.error("Failed to process message for sessionId: {}: {}", session.getId(), e.toString());
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

    private String extractPayloadText(JsonNode payload) {
        JsonNode textNode = payload.get("text");
        if (textNode == null) {
            return null;
        }
        String value = textNode.asText();
        return value != null ? value.trim() : null;
    }
}
