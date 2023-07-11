package com.itachallenge.user.hash;

import com.itachallenge.user.document.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class UserHashTest {
    @Test
    public void testGetterAndSetterMethods() {
        // Given
        UserHash user = UserHash.builder()
                .uuid(UUID.randomUUID())
                .name("John")
                .surname("Doe")
                .nickname("johndoe")
                .email("johndoe@example.com")
                .password("password123")
                .roles(Arrays.asList(Role.ADMIN, Role.USER))
                .build();

        // Assert
        Assertions.assertEquals(user.getUuid(), user.getUuid());
        Assertions.assertEquals(user.getName(), "John");
        Assertions.assertEquals(user.getSurname(), "Doe");
        Assertions.assertEquals(user.getNickname(), "johndoe");
        Assertions.assertEquals(user.getEmail(), "johndoe@example.com");
        Assertions.assertEquals(user.getPassword(), "password123");
        Assertions.assertEquals(user.getRoles(), Arrays.asList(Role.ADMIN, Role.USER));

        // Modify
        UUID newUuid = UUID.randomUUID();
        user.setUuid(newUuid);
        user.setName("Jane");
        user.setSurname("Smith");
        user.setNickname("janesmith");
        user.setEmail("janesmith@example.com");
        user.setPassword("newpassword");
        List<Role> newRoles = Arrays.asList(Role.USER);
        user.setRoles(newRoles);

        // Assert
        Assertions.assertEquals(user.getUuid(), newUuid);
        Assertions.assertEquals(user.getName(), "Jane");
        Assertions.assertEquals(user.getSurname(), "Smith");
        Assertions.assertEquals(user.getNickname(), "janesmith");
        Assertions.assertEquals(user.getEmail(), "janesmith@example.com");
        Assertions.assertEquals(user.getPassword(), "newpassword");
        Assertions.assertEquals(user.getRoles(), newRoles);
    }

    @Test
    public void testEqualsAndHashCodeMethods() {
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
