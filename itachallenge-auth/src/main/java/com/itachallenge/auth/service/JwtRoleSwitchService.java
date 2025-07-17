package com.itachallenge.auth.service;

import com.itachallenge.auth.enums.UserRole;
import com.itachallenge.jwtcore.service.IJwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class JwtRoleSwitchService {

    private final IJwtService jwtService;

    public JwtRoleSwitchService(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String switchRole(String token, String requestedRole) {
        Claims claims = jwtService.extractAllClaims(token);
        String currentRole = claims.get("role", String.class);

        UserRole.validateRoleChange(currentRole, requestedRole);

        return jwtService.generateToken(
                claims.getSubject(),
                requestedRole.toUpperCase(),
                claims.get("uuid", String.class)
        );
    }
}
