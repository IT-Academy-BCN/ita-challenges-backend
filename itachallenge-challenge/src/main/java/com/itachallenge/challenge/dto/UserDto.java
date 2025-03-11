package com.itachallenge.challenge.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID uuid;
    private String username;
    private String role;
}
