package org.ashkan.ghaffari.ingestor.ws;

import org.ashkan.ghaffari.ingestor.ws.inbound.IngestWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    private final IngestWebSocketHandler handler;

    public WebSocketConfig(IngestWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws-ingest")
            .setAllowedOriginPatterns("*");
        log.info("WebSocket handler registered");
    }
}
