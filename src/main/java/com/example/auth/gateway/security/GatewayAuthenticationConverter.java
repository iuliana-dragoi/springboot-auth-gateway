package com.example.auth.gateway.security;

import com.example.auth.gateway.strategy.AuthType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/api/auth/login")) {
            return Mono.empty();
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String authTypeHeader = exchange.getRequest().getHeaders().getFirst("X-Auth-Type");

        if (authHeader == null || authTypeHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String token = authHeader.substring(7);
        AuthType authType = AuthType.valueOf(authTypeHeader.toUpperCase());
        return Mono.just(new GatewayAuthenticationToken(token, authType));
    }
}
