package org.ashkan.ghaffari.ingestor.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final Map<String, Set<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToChat = new ConcurrentHashMap<>();

    public void registerSession(String chatId, WebSocketSession session) {
        chatSessions
            .computeIfAbsent(chatId, id -> ConcurrentHashMap.newKeySet())
            .add(session);
        sessionToChat.put(session.getId(), chatId);
    }

    public void removeSession(WebSocketSession session) {
        String chatId = sessionToChat.remove(session.getId());
        if (chatId == null) {
            return;
        }

        chatSessions.computeIfPresent(chatId, (id, set) -> {
            set.remove(session);
            return set.isEmpty() ? null : set;
        });
    }

    public Set<WebSocketSession> all(String chatId) {
        return chatSessions.getOrDefault(chatId, Collections.emptySet());
    }

    public String getChatId(WebSocketSession session) {
        return (String) session.getAttributes().get(ChatIdHandshakeInterceptor.CHAT_ID_ATTR);
    }

}
