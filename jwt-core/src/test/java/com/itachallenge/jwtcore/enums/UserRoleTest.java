package com.itachallenge.jwtcore.enums;

import com.itachallenge.jwtcore.exception.InvalidRoleChangeRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void validateRoleChange_validChange_ADMIN_to_USER_shouldPass() {
        assertDoesNotThrow(() -> UserRole.validateRoleChange("ADMIN", "USER"));
    }

    @Test
    void validateRoleChange_validChange_USER_to_ADMIN_shouldPass() {
        assertDoesNotThrow(() -> UserRole.validateRoleChange("user", "admin"));
    }

    @Test
    void validateRoleChange_nullRequestedRole_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", null)
        );
        assertEquals("New role must be provided.", ex.getMessage());
    }

    @Test
    void validateRoleChange_blankRequestedRole_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", " ")
        );
        assertEquals("New role must be provided.", ex.getMessage());
    }

    @Test
    void validateRoleChange_invalidRequestedRole_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", "GUEST")
        );
        assertEquals("Requested role change is not allowed.", ex.getMessage());
    }

    @Test
    void validateRoleChange_sameRole_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("USER", "user")
        );
        assertEquals("New role is the same as current role.", ex.getMessage());
    }

    @Test
    void validateRoleChange_invalidCurrentRole_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("GUEST", "ADMIN")
        );
        assertEquals("Current role is not allowed.", ex.getMessage());
    }

    @Test
    void validateRoleChange_nullCurrentRole_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange(null, "ADMIN")
        );
        assertEquals("Current role must be provided.", ex.getMessage());
    }

    @Test
    void validateRoleChange_invalidRequested_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", "GUEST")
        );
        assertEquals("Requested role change is not allowed.", ex.getMessage());
    }

    @Test
    void validateRoleChange_notAllowedTransition_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", "GUEST")
        );
        assertEquals("Requested role change is not allowed.", ex.getMessage());
    }

    @Test
    void validateAdminRoleChange_invalidTransition_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", "ADMIN")
        );
        assertEquals("New role is the same as current role.", ex.getMessage());
    }

    @Test
    void validateUserRoleChange_invalidTransition_shouldThrow() {
        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("USER", "USER")
        );
        assertEquals("New role is the same as current role.", ex.getMessage());
    }

    @Test
    void validateRoleChange_userToAdmin_thenAdminToUserAllowed_butOtherDirectionRejected() {
        assertDoesNotThrow(() -> UserRole.validateRoleChange("USER", "ADMIN"));

        assertDoesNotThrow(() -> UserRole.validateRoleChange("ADMIN", "USER"));

        InvalidRoleChangeRequestException ex = assertThrows(
                InvalidRoleChangeRequestException.class,
                () -> UserRole.validateRoleChange("ADMIN", "ADMIN")
        );
        assertEquals("New role is the same as current role.", ex.getMessage());
    }

}