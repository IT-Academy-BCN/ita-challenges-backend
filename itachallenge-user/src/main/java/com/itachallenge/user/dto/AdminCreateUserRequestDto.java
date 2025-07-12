package com.itachallenge.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class AdminCreateUserRequestDto {

    @NotBlank(message = "Username must not be blank")
    private String username;

}
