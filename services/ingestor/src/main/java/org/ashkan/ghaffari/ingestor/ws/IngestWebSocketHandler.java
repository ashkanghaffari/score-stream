package org.ashkan.ghaffari.ingestor.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class IngestWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(IngestWebSocketHandler.class);
    private static final int MAX_TEXT_SIZE = 64 * 1024;
    private static final String TOPIC = "text-ingest";

    private final KafkaTemplate<String, byte[]> kafka;
    private final ObjectMapper mapper;

    public IngestWebSocketHandler(KafkaTemplate<String, byte[]> kafka, ObjectMapper mapper) {
        this.kafka = kafka;
        this.mapper = mapper;
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
            IngestEvent incoming = mapper.readValue(json, IngestEvent.class);

            if (incoming.idempotencyId() == null || incoming.chatId() == null || incoming.userId() == null
                || incoming.type() == null) {
                close(session, CloseStatus.BAD_DATA);
                return;
            }

            IngestEvent enriched = new IngestEvent(
                incoming.idempotencyId(),
                incoming.type(),
                incoming.chatId(),
                incoming.userId(),
                incoming.timestamp(),
                incoming.payload()
            );

            byte[] serialized = mapper.writeValueAsBytes(enriched);
            kafka.send(TOPIC, enriched.chatId(), serialized)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.warn("Kafka send failed: {}", ex.toString());
                    }
                });

        } catch (Exception e) {
            log.debug("Bad frame from {}: {}", session.getId(), e.toString());
            close(session, CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket closed: {} ({})", session.getId(), status);
    }

    private void close(WebSocketSession s, CloseStatus status) {
        try { s.close(status); } catch (Exception ignored) {}
    }
}
