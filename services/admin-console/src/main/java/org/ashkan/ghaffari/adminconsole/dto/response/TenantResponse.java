package org.ashkan.ghaffari.adminconsole.dto.response;

public record TenantResponse(
   String tenantId,
   String name,
   String status,
   Long createdAt
) {}
