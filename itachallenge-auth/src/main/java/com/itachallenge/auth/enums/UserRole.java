package com.itachallenge.auth.enums;

import com.itachallenge.auth.exception.InvalidRoleChangeRequestException;

import java.util.Optional;

public enum UserRole {
    ADMIN,
    USER;

    public static Optional<UserRole> fromString(String value) {
        for (UserRole userRole : values()) {
            if (userRole.name().equalsIgnoreCase(value)) {
                return Optional.of(userRole);
            }
        }
        return Optional.empty();
    }

    public static void validateRoleChange(String currentRoleStr, String requestedRoleStr) {
        validateNotBlank(currentRoleStr, "Current role must be provided.");
        validateNotBlank(requestedRoleStr, "New role must be provided.");

        UserRole current = parseRole(currentRoleStr, "Current role is not allowed.");
        UserRole requested = parseRole(requestedRoleStr, "Requested role change is not allowed.");

        if (current == requested) {
            throw new InvalidRoleChangeRequestException("New role is the same as current role.");
        }

        if (!current.canSwitchTo(requested)) {
            throw new InvalidRoleChangeRequestException("Requested role change is not allowed.");
        }
    }

    private static void validateNotBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new InvalidRoleChangeRequestException(errorMessage);
        }
    }

    private static UserRole parseRole(String value, String errorMessage) {
        return fromString(value)
                .orElseThrow(() -> new InvalidRoleChangeRequestException(errorMessage));
    }

    private boolean canSwitchTo(UserRole requestedRole) {
        return (this == ADMIN && requestedRole == USER)
                || (this == USER && requestedRole == ADMIN);
    }
}