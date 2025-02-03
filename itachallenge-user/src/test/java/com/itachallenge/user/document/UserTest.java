package com.itachallenge.user.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class UserDocumentTest {

    @Test
    void noArgsConstructor() {
        UserDocument user = new UserDocument();
        assertNotNull(user);
    }

    @Test
    void allArgsConstructor() {
        UUID uuid = UUID.randomUUID();
        String username = "testUser";

        UserDocument user = new UserDocument(uuid, username);

        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
    }

    @Test
    void settersAndGetters() {
        UserDocument user = new UserDocument();
        UUID uuid = UUID.randomUUID();
        String username = "newUser";

        user.setUuid(uuid);
        user.setUsername(username);

        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
    }

    @Test
    void builder() {
        UUID uuid = UUID.randomUUID();
        String username = "builderUser";

        UserDocument user = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .build();

        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
    }

    @Test
    void equalsAndHashCode() {
        UUID uuid = UUID.randomUUID();
        String username = "equalTest";

        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, username);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        UUID uuid = UUID.randomUUID();
        String username = "stringTest";

        UserDocument user = new UserDocument(uuid, username);
        String userString = user.toString();

        assertTrue(userString.contains(uuid.toString()));
        assertTrue(userString.contains(username));
    }
}