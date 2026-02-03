package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.adminconsole.entity.Integration;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, IntegrationId> {
    default Integration find(String tenantId, String integrationId) {
        return findById(new IntegrationId(tenantId, integrationId)).orElse(null);
    }

    List<Integration> findByTenantId(String tenantId);

}
