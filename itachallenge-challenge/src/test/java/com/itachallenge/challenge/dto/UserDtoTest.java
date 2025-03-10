package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserDtoTest {

    private ObjectMapper mapper;

    private UUID uuid;
    private String username;
    private String role;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        uuid = UUID.randomUUID();
        username = "testUser";
        role = "MENTOR";
    }

    @Test
    @DisplayName("Test UserDto AllArgsConstructor")
    void testAllArgsConstructor() {
        UserDto userDto = new UserDto(uuid, username, role);

        assertThat(userDto.getUuid()).isEqualTo(uuid);
        assertThat(userDto.getUsername()).isEqualTo(username);
        assertThat(userDto.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("Test UserDto NoArgsConstructor")
    void testNoArgsConstructor() {
        UserDto userDto = new UserDto();

        assertNull(userDto.getUuid());
        assertNull(userDto.getUsername());
        assertNull(userDto.getRole());
    }

    @Test
    @DisplayName("Test UserDto Setters and Getters")
    void testSettersAndGetters() {
        UserDto userDto = new UserDto();
        userDto.setUuid(uuid);
        userDto.setUsername(username);
        userDto.setRole(role);

        assertEquals(uuid, userDto.getUuid());
        assertEquals(username, userDto.getUsername());
        assertEquals(role, userDto.getRole());
    }

    @Test
    @DisplayName("Test UserDto Builder")
    void testBuilder() {
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
    @DisplayName("Test UserDto Equals and HashCode with Same Object")
    void testEqualsAndHashCodeSameObject() {
        UserDto userDto = new UserDto(uuid, username, role);

        assertTrue(userDto.equals(userDto)); // Test mateix objecte
        assertEquals(userDto.hashCode(), userDto.hashCode());
    }

    @Test
    @DisplayName("Test UserDto Equals and HashCode with Different Objects")
    void testEqualsAndHashCodeDifferentObjects() {
        UserDto userDto1 = new UserDto(uuid, username, role);
        UserDto userDto2 = new UserDto(uuid, username, role);
        UserDto userDto3 = new UserDto(UUID.randomUUID(), "otherUser", "ADMIN");

        assertThat(userDto1).isEqualTo(userDto2);
        assertThat(userDto1.hashCode()).isEqualTo(userDto2.hashCode());

        assertThat(userDto1).isNotEqualTo(userDto3);
        assertThat(userDto1.hashCode()).isNotEqualTo(userDto3.hashCode());
    }

    @Test
    @DisplayName("Test UserDto Equals with Null and Different Class")
    void testEqualsWithNullAndDifferentClass() {
        UserDto userDto = new UserDto(uuid, username, role);

        assertNotEquals(null, userDto); // Test amb objecte null
        assertNotEquals(userDto, "Una cadena"); // Test amb classe diferent
    }

    @Test
    @DisplayName("Test UserDto ToString")
    void testToString() {
        UserDto userDto = new UserDto(uuid, username, role);

        String expected = "UserDto(uuid=" + uuid + ", username=" + username + ", role=" + role + ")";
        assertThat(userDto.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test UserDto Serialization")
    @SneakyThrows(JsonProcessingException.class)
    void testSerialization() {
        UserDto userDto = new UserDto(uuid, username, role);

        String json = mapper.writeValueAsString(userDto);

        assertTrue(json.contains("\"uuid\":\"" + uuid + "\""));
        assertTrue(json.contains("\"username\":\"" + username + "\""));
        assertTrue(json.contains("\"role\":\"" + role + "\""));
    }

    @Test
    @DisplayName("Test UserDto Deserialization")
    @SneakyThrows(JsonProcessingException.class)
    void testDeserialization() {
        String json = "{\"uuid\":\"" + uuid + "\",\"username\":\"" + username + "\",\"role\":\"" + role + "\"}";

        UserDto userDto = mapper.readValue(json, UserDto.class);

        assertEquals(uuid, userDto.getUuid());
        assertEquals(username, userDto.getUsername());
        assertEquals(role, userDto.getRole());
    }

    @Test
    @DisplayName("Test UserDto Deserialization with Missing Fields")
    @SneakyThrows(JsonProcessingException.class)
    void testDeserializationWithMissingFields() {
        String json = "{\"username\":\"" + username + "\"}";

        UserDto userDto = mapper.readValue(json, UserDto.class);

        assertNull(userDto.getUuid());
        assertEquals(username, userDto.getUsername());
        assertNull(userDto.getRole());
    }

    @Test
    @DisplayName("Test UserDto with Null Values")
    void testUserDtoWithNullValues() {
        UserDto userDto = new UserDto(null, null, null);

        assertNull(userDto.getUuid());
        assertNull(userDto.getUsername());
        assertNull(userDto.getRole());
    }
}
