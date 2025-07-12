package com.itachallenge.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.user.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtServiceImpl implements IJwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Override
    public Mono<String> extractRoleFromToken(String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return Mono.error(new UnauthorizedException("Missing or invalid Authorization header"));
            }
            String rawToken = token.replace("Bearer ", "");
            String[] chunks = rawToken.split("\\.");
            if (chunks.length < 2) {
                return Mono.error(new UnauthorizedException("Invalid token format"));
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            String role = (String) claims.get("role");

            if (role == null) {
                return Mono.error(new UnauthorizedException("Role not found in token"));
            }
            return Mono.just(role);

        } catch (Exception e) {
            log.warn("Failed to extract role from token: {}", e.getMessage());
            return Mono.error(new UnauthorizedException("Invalid or malformed token"));
        }
    }
}