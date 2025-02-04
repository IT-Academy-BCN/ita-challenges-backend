package com.itachallenge.user.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class UserDocumentTest {

    private UUID uuid;
    private String username;
    private UserDocument userDocument;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "testUser";
        userDocument = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .build();
    }

    @Test
    void userDocumentCreation() {
        assertNotNull(userDocument);
        assertEquals(uuid, userDocument.getUuid());
        assertEquals(username, userDocument.getUsername());
    }

    @Test
    void settersAndGetters() {
        UUID newUuid = UUID.randomUUID();
        String newUsername = "newUser";

        userDocument.setUuid(newUuid);
        userDocument.setUsername(newUsername);

        assertEquals(newUuid, userDocument.getUuid());
        assertEquals(newUsername, userDocument.getUsername());
    }

    @Test
    void builderPattern() {
        UserDocument user = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .build();

        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
    }

    @Test
    void noArgsConstructor() {
        UserDocument emptyUser = new UserDocument();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getUuid());
        assertNull(emptyUser.getUsername());
    }

    @Test
    void allArgsConstructor() {
        UserDocument user = new UserDocument(uuid, username);
        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
    }

    @Test
    void equalsAndHashCode() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, username);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setUsername("anotherUser");
        assertNotEquals(user1, user2);
    }

    @Test
    void testToString() {
        String toString = userDocument.toString();
        assertTrue(toString.contains("UserDocument"), "ToString should contain class name");
        assertTrue(toString.contains(uuid.toString()), "ToString should contain UUID");
        assertTrue(toString.contains(username), "ToString should contain username");
    }

    @Test
    void equalsWithSameObject() {
        assertEquals(userDocument, userDocument);
    }

    @Test
    void equalsWithSameHashCode() {
        assertEquals(userDocument.hashCode(), userDocument.hashCode());
    }

    @Test
    void equalsAndHashCodeWithDifferentUUIDs() {
        UserDocument differentUser = new UserDocument(UUID.randomUUID(), username);

        assertNotEquals(userDocument, differentUser);
        assertNotEquals(userDocument.hashCode(), differentUser.hashCode());
    }

    @Test
    void equalsAndHashCodeWithDifferentUsernames() {
        UserDocument sameUuidDifferentUsername = new UserDocument(uuid, "differentUser");

        assertNotEquals(userDocument, sameUuidDifferentUsername);
        assertNotEquals(userDocument.hashCode(), sameUuidDifferentUsername.hashCode());
    }

    @Test
    void equalsAndHashCodeWithNullFields() {
        UserDocument userWithNullUuid = new UserDocument(null, username);
        UserDocument userWithNullUsername = new UserDocument(uuid, null);
        UserDocument completelyNullUser = new UserDocument(null, null);

        assertNotEquals(userDocument, userWithNullUuid);
        assertNotEquals(userDocument, userWithNullUsername);
        assertNotEquals(userDocument, completelyNullUser);

        assertNotEquals(userDocument.hashCode(), userWithNullUuid.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullUsername.hashCode());
        assertNotEquals(userDocument.hashCode(), completelyNullUser.hashCode());
    }

    @Test
    void equalsWithDifferentClass() {
        Object otherObject = new Object();
        assertNotEquals(userDocument, otherObject);
    }

    @Test
    void equalsWithNull() {
        assertNotEquals(null, userDocument);
    }

    @Test
    void equalsConsistencyTest() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, username);

        assertEquals(user1, user2);
        assertEquals(user1, user2); // Repeated check for consistency
    }

    @Test
    void hashCodeConsistencyTest() {
        int initialHashCode = userDocument.hashCode();
        assertEquals(initialHashCode, userDocument.hashCode());
    }

    @Test
    void equalsTransitivityTest() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, username);
        UserDocument user3 = new UserDocument(uuid, username);

        assertEquals(user1, user2);
        assertEquals(user2, user3);
        assertEquals(user1, user3);
    }

    @Test
    void equalsSymmetryTest() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, username);

        assertEquals(user1, user2);
        assertEquals(user2, user1);
    }

    @Test
    void hashCodeEqualityForEqualObjects() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, username);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCodeDifferenceForNonEqualObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "user1");
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "user2");

        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithNullUuidAndUsername() {
        UserDocument user1 = new UserDocument(null, null);
        UserDocument user2 = new UserDocument(null, null);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUuid() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(null, username);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUsername() {
        UserDocument user1 = new UserDocument(uuid, username);
        UserDocument user2 = new UserDocument(uuid, null);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toStringHandlesNullValues() {
        UserDocument user = new UserDocument(null, null);
        String toString = user.toString();

        assertTrue(toString.contains("UserDocument"), "ToString should contain class name");
        assertFalse(toString.contains("uuid="), "ToString should not contain 'uuid=' when null");
        assertFalse(toString.contains("username="), "ToString should not contain 'username=' when null");
        assertEquals("UserDocument{}", toString, "ToString should return an empty object representation");
    }


    @Test
    void builderHandlesNullValues() {
        UserDocument user = UserDocument.builder()
                .uuid(null)
                .username(null)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
    }

    @Test
    void settersHandleNullValues() {
        userDocument.setUuid(null);
        userDocument.setUsername(null);

        assertNull(userDocument.getUuid());
        assertNull(userDocument.getUsername());
    }

    @Test
    void hashCodeDifferentForDifferentObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "UserA");
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "UserB");

        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void builderHandlesOnlyUuid() {
        UserDocument user = UserDocument.builder()
                .uuid(uuid)
                .build();

        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertNull(user.getUsername());
    }

    @Test
    void builderHandlesOnlyUsername() {
        UserDocument user = UserDocument.builder()
                .username(username)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertEquals(username, user.getUsername());
    }

    @Test
    void builderCreatesNewInstances() {
        UserDocument user1 = UserDocument.builder().uuid(uuid).username(username).build();
        UserDocument user2 = UserDocument.builder().uuid(uuid).username(username).build();

        assertNotSame(user1, user2);
        assertEquals(user1, user2);
    }

    @Test
    void builderWithoutParametersCreatesValidObject() {
        UserDocument user = UserDocument.builder().build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
    }

    @Test
    void modifyingBuiltObjectDoesNotAffectOriginalBuilder() {
        UserDocument.UserDocumentBuilder builder = UserDocument.builder().uuid(uuid).username(username);

        UserDocument user1 = builder.build();
        UserDocument user2 = builder.uuid(UUID.randomUUID()).username("newUser").build();

        assertNotEquals(user1, user2);
        assertNotEquals(user1.getUuid(), user2.getUuid());
        assertNotEquals(user1.getUsername(), user2.getUsername());
    }



}

