package com.itachallenge.challenge.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoTest {

    private UUID uuid;
    private String username;
    private String role;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "testUser";
        role = "MENTOR";
    }

    @Test
    void testUserDto_AllArgsConstructor() {
        UserDto userDto = new UserDto(uuid, username, role);

        assertThat(userDto.getUuid()).isEqualTo(uuid);
        assertThat(userDto.getUsername()).isEqualTo(username);
        assertThat(userDto.getRole()).isEqualTo(role);
    }

    @Test
    void testUserDto_NoArgsConstructor() {
        UserDto userDto = new UserDto();

        assertThat(userDto.getUuid()).isNull();
        assertThat(userDto.getUsername()).isNull();
        assertThat(userDto.getRole()).isNull();
    }

    @Test
    void testUserDto_SettersAndGetters() {
        UserDto userDto = new UserDto();
        userDto.setUuid(uuid);
        userDto.setUsername(username);
        userDto.setRole(role);

        assertThat(userDto.getUuid()).isEqualTo(uuid);
        assertThat(userDto.getUsername()).isEqualTo(username);
        assertThat(userDto.getRole()).isEqualTo(role);
    }

    @Test
    void testUserDto_Builder() {
        UserDto userDto = UserDto.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .build();

        assertThat(userDto.getUuid()).isEqualTo(uuid);
        assertThat(userDto.getUsername()).isEqualTo(username);
        assertThat(userDto.getRole()).isEqualTo(role);
    }

    @Test
    void testUserDto_EqualsAndHashCode() {
        UserDto userDto1 = new UserDto(uuid, username, role);
        UserDto userDto2 = new UserDto(uuid, username, role);

        assertThat(userDto1).isEqualTo(userDto2);
        assertThat(userDto1.hashCode()).isEqualTo(userDto2.hashCode());
    }

    @Test
    void testUserDto_ToString() {
        UserDto userDto = new UserDto(uuid, username, role);

        String expected = "UserDto(uuid=" + uuid + ", username=" + username + ", role=" + role + ")";
        assertThat(userDto.toString()).isEqualTo(expected);
    }
}
