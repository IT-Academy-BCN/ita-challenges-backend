package com.itachallenge.auth.service;

import com.itachallenge.auth.enums.UserRole;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class JwtRoleSwitchService {

    private final IAuthJwtFacade authJwtFacade;

    public JwtRoleSwitchService(IAuthJwtFacade authJwtFacade) {
        this.authJwtFacade = authJwtFacade;
    }

    public String switchRole(String token, String requestedRole) {
        Claims claims = authJwtFacade.extractAllClaims(token);
        String currentRole = claims.get("role", String.class);

        UserRole.validateRoleChange(currentRole, requestedRole);

        return authJwtFacade.generateToken(
                claims.getSubject(),
                requestedRole.toUpperCase(),
                claims.get("uuid", String.class)
        );
    }
}
