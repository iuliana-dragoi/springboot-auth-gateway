package com.example.auth.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

@Configuration
@EnableMethodSecurity
public class SpringSecurity {

    private final GatewayAuthenticationManager authenticationManager;
    private final GatewayAuthenticationConverter converter;

    public SpringSecurity(GatewayAuthenticationManager authenticationManager, GatewayAuthenticationConverter converter) {
        this.authenticationManager = authenticationManager;
        this.converter = converter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, GatewayAuthenticationManager authenticationManager, GatewayAuthenticationConverter converter) {

        AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authenticationManager);
        authFilter.setServerAuthenticationConverter(converter);
        authFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .authorizeExchange(auth -> auth
                .pathMatchers("/api/auth/login/**").permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
