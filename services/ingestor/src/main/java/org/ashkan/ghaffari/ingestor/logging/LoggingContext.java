package org.ashkan.ghaffari.ingestor.logging;

import org.slf4j.MDC;

public final class LoggingContext {

    private LoggingContext() {}

    public static void withContext(String tenantId, String appId, String chatId, String messageId, String idempotencyId, Runnable runnable) {
        if (tenantId != null) MDC.put("tenantId", tenantId);
        if (appId != null) MDC.put("appId", appId);
        if (chatId != null) MDC.put("chatId", chatId);
        if (messageId != null) MDC.put("messageId", messageId);
        if (idempotencyId != null) MDC.put("idempotencyId", idempotencyId);
        try {
            runnable.run();
        } finally {
            MDC.remove("tenantId");
            MDC.remove("appId");
            MDC.remove("chatId");
            MDC.remove("messageId");
            MDC.remove("idempotencyId");
        }
    }
}
