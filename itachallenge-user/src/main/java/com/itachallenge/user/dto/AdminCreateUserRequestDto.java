package com.itachallenge.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminCreateUserRequestDto {

    @NotEmpty(message = "Usernames list must cannot be empty")
    private List <String> usernames;
}
