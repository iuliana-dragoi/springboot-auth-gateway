package com.example.auth.gateway.strategy;

import com.example.auth.gateway.auth.AuthenticatedUser;
import com.example.auth.gateway.service.JwtService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CustomJwtAuthenticationStrategy implements AuthenticationStrategy {

    private final JwtService jwtService;

    public CustomJwtAuthenticationStrategy(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean canHandle(AuthType authType) {
        return authType == AuthType.JWT_CUSTOM;
    }

    @Override
    public Mono<AuthenticatedUser> authenticate(String token) throws Exception {
//        String token = extractToken(request);
        String username = jwtService.extractUsername(token);
        List<String> roles = jwtService.extractRoles(token);
        return Mono.just(new AuthenticatedUser(username, roles, "CUSTOM"));
    }

    private String extractToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        throw new RuntimeException("No token found");
    }

}
