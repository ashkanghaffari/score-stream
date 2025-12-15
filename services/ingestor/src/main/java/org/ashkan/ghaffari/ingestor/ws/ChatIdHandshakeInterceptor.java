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

public class ChatIdHandshakeInterceptor implements HandshakeInterceptor {

    public static final String TENANT_ID_ATTR = "tenantId";
    public static final String APP_ID_ATTR = "appId";
    public static final String CHAT_ID_ATTR = "chatId";
    public static final String CHAT_SCOPE_KEY_ATTR = "chatScopeKey";
    private static final Logger log = LoggerFactory.getLogger(ChatIdHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.warn("Rejected WebSocket handshake: not a servlet request");
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        String tenantId = getParam(servletRequest, TENANT_ID_ATTR);
        String appId = getParam(servletRequest, APP_ID_ATTR);
        String chatId = getParam(servletRequest, CHAT_ID_ATTR);

        if (tenantId == null || appId == null || chatId == null) {
            log.warn("Rejected WebSocket handshake missing required params tenantId/appId/chatId");
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        attributes.put(TENANT_ID_ATTR, tenantId);
        attributes.put(APP_ID_ATTR, appId);
        attributes.put(CHAT_ID_ATTR, chatId);
        attributes.put(CHAT_SCOPE_KEY_ATTR, buildScopeKey(tenantId, appId, chatId));
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }

    private String getParam(ServletServerHttpRequest request, String name) {
        String value = request.getServletRequest().getParameter(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    public static String buildScopeKey(String tenantId, String appId, String chatId) {
        return tenantId + ":" + appId + ":" + chatId;
    }
}
