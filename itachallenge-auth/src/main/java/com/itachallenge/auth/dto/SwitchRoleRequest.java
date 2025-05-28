package com.itachallenge.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to request a temporary role switch.")
public class SwitchRoleRequest {

    @Schema(
            description = "The new role to switch to. Allowed values: ADMIN, USER.",
            example = "ADMIN",
            required = true
    )
    private String newRole;

    public SwitchRoleRequest() {
    }

    public SwitchRoleRequest(String newRole) {
        this.newRole = newRole;
    }

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }
}