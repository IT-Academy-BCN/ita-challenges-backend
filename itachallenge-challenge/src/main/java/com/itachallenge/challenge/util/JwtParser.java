package com.itachallenge.challenge.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtParser {

    private static final Logger log = LoggerFactory.getLogger(JwtParser.class);


    public String extractUuid(String token) {
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
