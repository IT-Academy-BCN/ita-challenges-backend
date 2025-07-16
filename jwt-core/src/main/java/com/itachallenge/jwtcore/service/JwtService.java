package com.itachallenge.jwtcore.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.itachallenge.jwtcore.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.Map;


@Service
public class JwtService implements IJwtService {

    private static final String BEARER_KEY = "Bearer ";
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final String jwtSigningKey;
    private final long minutesTillExpiration;

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
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            log.info("Logout with expired token: {}", e.getMessage());
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token expired but logout successful", e);
        } catch (JwtException e) {
            log.warn("Logout attempt with invalid or tampered token: {}", e.getMessage());
            throw new JwtException("Invalid or tampered token: " + e.getMessage(), e);
        }
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.info("Token expired at {} for subject {}",
                    e.getClaims().getExpiration(),
                    e.getClaims().getSubject());
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token expired ", e);
        } catch (JwtException e) {
            log.warn("Invalid or tampered token: {}", e.getMessage());
            throw new JwtException("Invalid or tampered token: " + e.getMessage(), e);
        }
    }

    public String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_KEY)) {
            throw new JwtException("Authorization header is missing or malformed");
        }
        return authHeader.replace(BEARER_KEY, "").trim();
    }

    @Override
    public String switchRole(String token, String requestedRole) {
        Claims claims = extractAllClaims(token);
        String currentRole = claims.get("role", String.class);

        UserRole.validateRoleChange(currentRole, requestedRole);

        return Jwts.builder()
                .subject(claims.getSubject())
                .claim("role", requestedRole.toUpperCase())
                .claim("uuid", claims.get("uuid", String.class))
                .claim("isTemporaryRole", true)
                .issuedAt(claims.getIssuedAt())
                .expiration(claims.getExpiration())
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String getUserUuIdFromAuthenticationHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Missing or bad formatted Authorization header");
        }
        String userId = extractUuid(authHeader.replace("Bearer ", ""));
        if (userId == null) {
            throw new JwtException("Invalid Authorization header content");
        }
        return userId;
    }

    private String extractUuid(String token) {
        try {
            return extractAllClaims(token).get("uuid").toString();  // Get "uuid" claim
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return null;
        }
    }

    private static Map<String, Object> extractAllClaimsMap(String token) throws IOException {
        int n1 = token.indexOf(".");
        int n2 = token.lastIndexOf(".");
        String claimsBase64 = token.substring(n1 + 1, n2);
        byte[] claimsByte = Decoders.BASE64.decode(claimsBase64);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(claimsByte, Map.class);
    }

}
