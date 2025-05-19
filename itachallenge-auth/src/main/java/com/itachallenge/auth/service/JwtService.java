package com.itachallenge.auth.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.ExpiredJwtException;


import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService implements IJwtService {

    private final String jwtSigningKey;
    private final long minutesTillExpiration;
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    public JwtService(
            @Value("${token.signing.key}") String jwtSigningKey,
            @Value("${token.expiration.minutes}") Long minutesTillExpiration) {
        this.jwtSigningKey = jwtSigningKey;
        this.minutesTillExpiration = minutesTillExpiration;
    }

    @Override
    public String generateToken(String username, String role, String uuid) {
        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("uuid", uuid)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + minutesTillExpiration * 60000))
                .signWith(getSigningKey());
        return builder.compact();
    }

    @Override
    public void validateToken(String token) {
        SecretKey key = getSigningKey();
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.warn("Expired Token: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("Invalid Token: {}", e.getMessage());
            throw e;
        }
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
