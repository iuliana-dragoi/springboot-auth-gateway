package com.example.auth.gateway.security;

import com.example.auth.gateway.strategy.AuthType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class GatewayAuthenticationToken extends AbstractAuthenticationToken {

    private final String authHeader;
    private final AuthType authType;

    public GatewayAuthenticationToken(String authHeader, AuthType authType) {
        super(null);
        this.authHeader = authHeader;
        this.authType = authType;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return authHeader;
    }

    @Override
    public Object getPrincipal() {
        return authHeader;
    }

}
