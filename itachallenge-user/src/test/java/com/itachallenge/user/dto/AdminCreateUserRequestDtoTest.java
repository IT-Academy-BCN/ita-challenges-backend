package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AdminCreateUserRequestDtoTest {

    @Test
    void testNoArgsConstructor() {
        AdminCreateUserRequestDto dto = new AdminCreateUserRequestDto();
        assertNotNull(dto);
    }

    @Test
    void testSetAndGetUsernames() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        List<String> usernames = List.of("user1", "user2", "user3");

        request.setUsernames(usernames);

        assertEquals(usernames, request.getUsernames());
    }

    @Test
    void testSetAndGetEmptyUsernames() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        List<String> emptyList = Collections.emptyList();

        request.setUsernames(emptyList);

        assertEquals(emptyList, request.getUsernames());
    }
}