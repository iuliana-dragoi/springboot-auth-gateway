package com.example.auth.gateway.security;

import com.example.auth.gateway.strategy.AuthenticationStrategy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GatewayAuthenticationManager implements ReactiveAuthenticationManager {

    private final List<AuthenticationStrategy> strategies;

    public GatewayAuthenticationManager(List<AuthenticationStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        GatewayAuthenticationToken token = (GatewayAuthenticationToken) authentication;

        AuthenticationStrategy strategy = strategies.stream()
                .filter(s -> s.canHandle(token.getAuthType()))
                .findFirst()
                .orElseThrow();

        try {
            return strategy.authenticate(token.getAuthHeader())
                .map(user -> new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.roles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
