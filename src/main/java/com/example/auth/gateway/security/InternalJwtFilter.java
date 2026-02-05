package com.example.auth.gateway.security;

import com.example.auth.gateway.auth.AuthenticatedUser;
import com.example.auth.gateway.service.InternalTokenService;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
public class InternalJwtFilter implements GlobalFilter, Ordered {

    private final InternalTokenService internalTokenService;

    public InternalJwtFilter(InternalTokenService internalTokenService) {
        this.internalTokenService = internalTokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (exchange.getRequest().getPath().toString().startsWith("/api/auth/login")) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthenticatedUser) ctx.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    String internalJwt = internalTokenService.issue(user);
                    ServerHttpRequest mutated = exchange.getRequest()
                            .mutate()
                            .header("Authorization", "Bearer " + internalJwt)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
