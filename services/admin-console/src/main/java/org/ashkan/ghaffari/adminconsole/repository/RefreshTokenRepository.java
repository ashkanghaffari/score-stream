package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.adminconsole.entity.RefreshToken;
import org.ashkan.ghaffari.adminconsole.entity.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTenantUser(TenantUser tenantUser);

    List<RefreshToken> findAllByTenantUser(TenantUser tenantUser);
}
