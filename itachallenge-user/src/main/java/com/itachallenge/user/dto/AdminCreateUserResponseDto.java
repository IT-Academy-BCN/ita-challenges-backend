package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AdminCreateUserResponseDto {

    @JsonProperty("created_users")
    private List<UserCreatedDto> createdUsers;

    @JsonProperty("existing_users")
    private List<String> existingUsers;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class UserCreatedDto {
        @JsonProperty("uuid")
        private UUID uuid;
        @JsonProperty("username")
        private String username;
    }
}