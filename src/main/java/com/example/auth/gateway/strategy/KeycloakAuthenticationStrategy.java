package com.example.auth.gateway.strategy;

import com.example.auth.gateway.auth.AuthenticatedUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class KeycloakAuthenticationStrategy implements AuthenticationStrategy {

    private final ReactiveJwtDecoder jwtDecoder;

    public KeycloakAuthenticationStrategy(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean canHandle(AuthType authType) {
        return authType == AuthType.KEYCLOAK;
    }

    @Override
    public Mono<AuthenticatedUser> authenticate(String token) throws Exception {
//        String token = extractToken(request);
        return jwtDecoder.decode(token)
                .map(jwt -> {
                    @SuppressWarnings("unchecked")
                    List<String> roles = ((List<String>) jwt.getClaimAsMap("realm_access")
                            .getOrDefault("roles", List.of()));
                    return new AuthenticatedUser(jwt.getSubject(), roles, "KEYCLOAK");
                });
    }

    private String extractToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        throw new RuntimeException("No token found");
    }
}
