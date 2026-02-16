package org.ashkan.ghaffari.adminconsole.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin.security.jwt")
public class JwtTokenProperties {

    private String secret;
    private String issuer;
    private long accessTokenLifetimeSec;
    private long refreshTokenLifetimeDays;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getAccessTokenLifetimeSec() {
        return accessTokenLifetimeSec;
    }

    public void setAccessTokenLifetimeSec(long accessTokenLifetimeSec) {
        this.accessTokenLifetimeSec = accessTokenLifetimeSec;
    }

    public long getRefreshTokenLifetimeDays() {
        return refreshTokenLifetimeDays;
    }

    public void setRefreshTokenLifetimeDays(long refreshTokenLifetimeDays) {
        this.refreshTokenLifetimeDays = refreshTokenLifetimeDays;
    }
}
