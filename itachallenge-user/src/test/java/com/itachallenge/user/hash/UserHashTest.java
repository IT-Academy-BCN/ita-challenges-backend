package com.itachallenge.user.hash;

import com.itachallenge.user.document.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class UserHashTest {
    @Test
    void testGetterAndSetterMethods() {
        UUID originUuid = UUID.randomUUID();
        // Given
        UserHash user = UserHash.builder()
                .dni("12345678A")
                .uuid(originUuid)
                .name("John")
                .surname("Doe")
                .nickname("johndoe")
                .email("johndoe@example.com")
                .password("password123")
                .roles(Arrays.asList(Role.ADMIN, Role.USER))
                .build();

        // Assert
        Assertions.assertEquals(originUuid, user.getUuid());
        Assertions.assertEquals("12345678A", user.getDni());
        Assertions.assertEquals("John", user.getName());
        Assertions.assertEquals("Doe", user.getSurname());
        Assertions.assertEquals("johndoe", user.getNickname());
        Assertions.assertEquals("johndoe@example.com", user.getEmail());
        Assertions.assertEquals("password123", user.getPassword());
        Assertions.assertEquals(Arrays.asList(Role.ADMIN, Role.USER), user.getRoles());

        // Modify
        UUID newUuid = UUID.randomUUID();
        user.setUuid(newUuid);
        user.setDni("23456789B");
        user.setName("Jane");
        user.setSurname("Smith");
        user.setNickname("janesmith");
        user.setEmail("janesmith@example.com");
        user.setPassword("newpassword");
        List<Role> newRoles = Arrays.asList(Role.USER);
        user.setRoles(newRoles);

        // Assert
        Assertions.assertEquals(newUuid, user.getUuid());
        Assertions.assertEquals("23456789B", user.getDni());
        Assertions.assertEquals("Jane", user.getName());
        Assertions.assertEquals("Smith", user.getSurname());
        Assertions.assertEquals("janesmith", user.getNickname());
        Assertions.assertEquals("janesmith@example.com", user.getEmail());
        Assertions.assertEquals("newpassword", user.getPassword());
        Assertions.assertEquals(newRoles, user.getRoles());
    }

    @Test
    void testEqualsAndHashCodeMethods() {
        // Given
        UserHash user1 = UserHash.builder()
                .uuid(UUID.randomUUID())
                .name("John")
                .surname("Doe")
                .nickname("johndoe")
                .email("johndoe@example.com")
                .password("password123")
                .roles(Arrays.asList(Role.ADMIN, Role.USER))
                .build();

        UserHash user2 = UserHash.builder()
                .uuid(user1.getUuid())
                .name("John")
                .surname("Doe")
                .nickname("johndoe")
                .email("johndoe@example.com")
                .password("password123")
                .roles(Arrays.asList(Role.ADMIN, Role.USER))
                .build();

        // Assert
        Assertions.assertEquals(user1, user2);
        Assertions.assertEquals(user1.hashCode(), user2.hashCode());

        // Modify
        user2.setName("Jane");

        // Assert
        Assertions.assertNotEquals(user1, user2);
        Assertions.assertNotEquals(user1.hashCode(), user2.hashCode());
    }
}
