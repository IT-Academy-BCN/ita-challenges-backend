package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

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
    @DisplayName("Test Setters and Getters")
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
    @DisplayName("Test Equals and HashCode with Same Object")
    void testEqualsAndHashCodeSameObject() {
        UserDto userDto = new UserDto(uuid, username, role);

        assertTrue(userDto.equals(userDto)); // Mateix objecte
        assertEquals(userDto.hashCode(), userDto.hashCode());
    }

    @Test
    @DisplayName("Test Equals and HashCode with Different Objects")
    void testEqualsAndHashCodeDifferentObjects() {
        UserDto userDto1 = new UserDto(uuid, username, role);
        UserDto userDto2 = new UserDto(uuid, username, role);
        UserDto userDto3 = new UserDto(UUID.randomUUID(), "otherUser", "ADMIN");

        assertEquals(userDto1, userDto2);
        assertNotEquals(userDto1, userDto3);
        assertEquals(userDto1.hashCode(), userDto2.hashCode());
        assertNotEquals(userDto1.hashCode(), userDto3.hashCode());
    }

    @Test
    @DisplayName("Test Equals with Null and Different Class")
    void testEqualsWithNullAndDifferentClass() {
        UserDto userDto = new UserDto(uuid, username, role);

        assertNotEquals(null, userDto);
        assertNotEquals(userDto, "Una cadena");
    }

    @Test
    @DisplayName("Test ToString")
    void testToString() {
        UserDto userDto = new UserDto(uuid, username, role);

        String expected = "UserDto(uuid=" + uuid + ", username=" + username + ", role=" + role + ")";
        assertEquals(expected, userDto.toString());
    }

    @Test
    @DisplayName("Test Serialization")
    @SneakyThrows(JsonProcessingException.class)
    void testSerialization() {
        UserDto userDto = new UserDto(uuid, username, role);

        String json = mapper.writeValueAsString(userDto);

        assertTrue(json.contains("\"uuid\":\"" + uuid + "\""));
        assertTrue(json.contains("\"username\":\"" + username + "\""));
        assertTrue(json.contains("\"role\":\"" + role + "\""));
    }

    @Test
    @DisplayName("Test Deserialization")
    @SneakyThrows(JsonProcessingException.class)
    void testDeserialization() {
        String json = "{\"uuid\":\"" + uuid + "\",\"username\":\"" + username + "\",\"role\":\"" + role + "\"}";

        UserDto userDto = mapper.readValue(json, UserDto.class);

        assertEquals(uuid, userDto.getUuid());
        assertEquals(username, userDto.getUsername());
        assertEquals(role, userDto.getRole());
    }

    @Test
    @DisplayName("Test Null Fields in UserDto")
    void testNullFields() {
        UserDto userDto = new UserDto(null, null, null);

        assertNull(userDto.getUuid());
        assertNull(userDto.getUsername());
        assertNull(userDto.getRole());
    }

    @Test
    @DisplayName("Test UserDto in Collection")
    void testUserDtoInCollection() {
        UserDto userDto = new UserDto(uuid, username, role);
        Set<UserDto> userSet = new HashSet<>();
        userSet.add(userDto);

        assertTrue(userSet.contains(userDto));
    }

    @Test
    @DisplayName("Test UserDto in Map Key")
    void testUserDtoAsMapKey() {
        UserDto userDto = new UserDto(uuid, username, role);
        Map<UserDto, String> userMap = new HashMap<>();
        userMap.put(userDto, "testValue");

        assertEquals("testValue", userMap.get(userDto));
    }
}
