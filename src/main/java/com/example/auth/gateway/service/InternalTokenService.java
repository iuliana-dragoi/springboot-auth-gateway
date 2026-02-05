package com.example.auth.gateway.service;

import com.example.auth.gateway.auth.AuthenticatedUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class InternalTokenService {

    public final InternalJwtProperties jwtProperties;

    public InternalTokenService(InternalJwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String issue(AuthenticatedUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration());
        Key signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));

        return Jwts.builder()
                .setSubject(user.username())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("roles", user.roles())
                .claim("authProvider", user.authProvider())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        Key signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));

        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
