package org.ashkan.ghaffari.adminconsole.dto.response;

public record SessionInfo(
    String tenantId,
    String role
) {}
