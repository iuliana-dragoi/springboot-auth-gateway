//package com.example.auth.gateway.security;
//
//import com.example.auth.gateway.auth.AuthenticatedUser;
//import com.example.auth.gateway.service.InternalTokenService;
//import com.example.auth.gateway.strategy.AuthType;
//import com.example.auth.gateway.strategy.AuthenticationStrategy;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//public class GatewayAuthFilter implements GlobalFilter, Ordered {
//
//    private final List<AuthenticationStrategy> strategies;
//    private final InternalTokenService internalTokenService;
//
//    public GatewayAuthFilter(List<AuthenticationStrategy> strategies, InternalTokenService internalTokenService) {
//        this.strategies = strategies;
//        this.internalTokenService = internalTokenService;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        if (exchange.getRequest().getPath().toString().startsWith("/api/auth/login")) {
//            return chain.filter(exchange);
//        }
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        AuthType authType = extractAuthType(exchange);
//        if(authType == null) {
//            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
//            return exchange.getResponse().setComplete();
//        }
//
//        AuthenticationStrategy strategy = strategies.stream()
//                .filter(s -> s.canHandle(authType))
//                .findFirst()
//                .orElseThrow();
//
//        try {
//            return strategy.authenticate(exchange.getRequest())
//                    .flatMap(user -> {
//                        String internalJwt = internalTokenService.issue(user);
//                        ServerHttpRequest mutated = exchange.getRequest()
//                                .mutate()
//                                .header("Authorization", "Bearer " + internalJwt)
//                                .build();
//                        return chain.filter(exchange.mutate().request(mutated).build());
//                    })
//                    .onErrorResume(e -> {
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return exchange.getResponse().setComplete();
//                    });
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return null;
//    }
//
//    @Override
//    public int getOrder() {
//        return -1;
//    }
//
//    private AuthType extractAuthType(ServerWebExchange exchange) {
//        String type = exchange.getRequest().getHeaders().getFirst("X-Auth-Type");
//        return type != null ? AuthType.valueOf(type.toUpperCase()) : null;
//    }
//}
