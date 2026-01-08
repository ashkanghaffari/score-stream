package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.adminconsole.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    default Tenant getById(String tenantId) {
        return findById(tenantId).orElse(null);
    }
}
