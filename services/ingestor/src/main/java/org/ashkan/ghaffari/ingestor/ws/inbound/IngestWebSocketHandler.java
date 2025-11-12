package org.ashkan.ghaffari.ingestor.ws.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.dynamo.chatmessage.ChatMessageService;
import org.ashkan.ghaffari.ingestor.redis.IdempotencyRepository;
import org.ashkan.ghaffari.ingestor.ws.ChatIdHandshakeInterceptor;
import org.ashkan.ghaffari.ingestor.ws.SessionRegistry;
import org.ashkan.ghaffari.ingestor.ws.dto.IngestMessage;
import org.ashkan.ghaffari.ingestor.ws.dto.ChatTextMessage;
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
    private static final String CLEAN_TOPIC = "text-clean";
    private static final String FLAGGED_TOPIC = "text-flagged";

    private final KafkaTemplate<String, byte[]> kafka;
    private final ObjectMapper mapper;
    private final IdempotencyRepository idempotencyRepository;
    private final SessionRegistry sessionRegistry;
    private final ChatMessageService chatMessageService;

    public IngestWebSocketHandler(KafkaTemplate<String, byte[]> kafka, ObjectMapper mapper,
                                  IdempotencyRepository idempotencyRepository, SessionRegistry sessionRegistry,
                                  ChatMessageService chatMessageService) {
        this.kafka = kafka;
        this.mapper = mapper;
        this.idempotencyRepository = idempotencyRepository;
        this.sessionRegistry = sessionRegistry;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.setTextMessageSizeLimit(MAX_TEXT_SIZE);
        String chatId = (String) session.getAttributes().get(ChatIdHandshakeInterceptor.CHAT_ID_ATTR);
        if (chatId == null) {
            log.warn("Closing WebSocket {} due to missing chatId attribute", session.getId());
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException e) {
                log.debug("Failed to close session {}: {}", session.getId(), e.getMessage());
            }
            return;
        }

        sessionRegistry.registerSession(chatId, session);
        log.info("WebSocket connected: {} (chatId={})", session.getId(), chatId);
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

            ChatTextMessage chatTextMessage = new ChatTextMessage(
                UUID.randomUUID(),
                incoming.idempotencyId(),
                incoming.type(),
                incoming.chatId(),
                incoming.senderId(),
                Instant.now(),
                incoming.payload()
            );

            byte[] serialized = mapper.writeValueAsBytes(chatTextMessage);
            kafka.send(CLEAN_TOPIC, chatTextMessage.chatId(), serialized)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.warn("Kafka send failed: {}", ex.toString());
                    }
                });

            chatMessageService.saveMessage(chatTextMessage);

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
            session.sendMessage(new org.springframework.web.socket.TextMessage("{\"error\":\"" + reason + "\"}"));
        } catch (Exception ignored) {}
    }
}
