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
        if (currentRoleStr == null || currentRoleStr.isBlank()) {
            throw new InvalidRoleChangeRequestException("Current role must be provided.");
        }

        if (requestedRoleStr == null || requestedRoleStr.isBlank()) {
            throw new InvalidRoleChangeRequestException("New role must be provided.");
        }

        Optional<UserRole> currentOpt = fromString(currentRoleStr);
        Optional<UserRole> requestedOpt = fromString(requestedRoleStr);

        if (currentOpt.isEmpty()) {
            throw new InvalidRoleChangeRequestException("Current role is not allowed.");
        }

        if (requestedOpt.isEmpty()) {
            throw new InvalidRoleChangeRequestException("Requested role change is not allowed.");
        }

        UserRole current = currentOpt.get();
        UserRole requested = requestedOpt.get();

        if (current == requested) {
            throw new InvalidRoleChangeRequestException("New role is the same as current role.");
        }

        if (!current.canSwitchTo(requested)) {
            throw new InvalidRoleChangeRequestException("Requested role change is not allowed.");
        }
    }

    private boolean canSwitchTo(UserRole requestedRole) {
        return (this == ADMIN && requestedRole == USER)
                || (this == USER && requestedRole == ADMIN);
    }
}