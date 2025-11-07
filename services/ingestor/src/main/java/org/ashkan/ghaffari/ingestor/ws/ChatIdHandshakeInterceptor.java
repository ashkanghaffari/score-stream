package org.ashkan.ghaffari.ingestor.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

public class ChatIdHandshakeInterceptor implements HandshakeInterceptor {

    public static final String CHAT_ID_ATTR = "chatId";
    private static final Logger log = LoggerFactory.getLogger(ChatIdHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        return extractChatId(request)
            .map(chatId -> {
                attributes.put(CHAT_ID_ATTR, chatId);
                return true;
            })
            .orElseGet(() -> {
                log.warn("Rejected WebSocket handshake without chatId");
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            });
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }

    private Optional<String> extractChatId(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String chatId = servletRequest.getServletRequest().getParameter(CHAT_ID_ATTR);
            if (chatId != null && !chatId.isBlank()) {
                return Optional.of(chatId);
            }
        }
        return Optional.empty();
    }
}
