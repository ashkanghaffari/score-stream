package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.adminconsole.entity.TenantUser;
import org.ashkan.ghaffari.adminconsole.entity.TenantUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, TenantUserId> {
    default TenantUser find(String tenantId, String userId) {
        return findById(new TenantUserId(tenantId, userId)).orElse(null);
    }

    List<TenantUser> findByEmail(String email);

    Optional<TenantUser> findByTenantIdAndEmail(String tenantId, String email);
}
