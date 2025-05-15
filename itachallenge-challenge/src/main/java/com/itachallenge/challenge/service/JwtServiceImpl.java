package com.itachallenge.challenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.exception.JwtException;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class JwtServiceImpl implements IJwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public String getUserUuIdFromAuthenticationHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new JwtException("Missing or bad formatted Authorization header");
        }
        String userId = extractUuid(authHeader.replace(BEARER_PREFIX, ""));
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

    private static Map<String, Object> extractAllClaims(String token) throws IOException {
        int n1 = token.indexOf(".");
        int n2 = token.lastIndexOf(".");
        String claimsBase64 = token.substring(n1 + 1, n2);
        byte[] claimsByte = Decoders.BASE64.decode(claimsBase64);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(claimsByte, Map.class);
    }
}
