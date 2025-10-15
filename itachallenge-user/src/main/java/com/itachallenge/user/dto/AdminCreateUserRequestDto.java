package com.itachallenge.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class AdminCreateUserRequestDto {

    @NotBlank(message = "{adminCreateUser.username.notBlank}")
    private String username;

}
