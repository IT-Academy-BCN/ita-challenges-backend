package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AdminCreateUserResponseDto {

    @JsonProperty(value ="uuid_user")
    private String userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("role")
    private String role;

}