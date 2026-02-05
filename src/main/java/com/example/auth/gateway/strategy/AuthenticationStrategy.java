package com.example.auth.gateway.strategy;

import com.example.auth.gateway.auth.AuthenticatedUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public interface AuthenticationStrategy {
    boolean canHandle(AuthType authType);
    Mono<AuthenticatedUser> authenticate(String token) throws Exception;
}
