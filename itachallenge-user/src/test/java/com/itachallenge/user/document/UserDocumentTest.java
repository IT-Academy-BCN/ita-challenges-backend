package com.itachallenge.user.document;

import com.itachallenge.user.document.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class UserDocumentTest {

    private UUID uuid;
    private String username;
    private Role role;
    private UserDocument userDocument;
    private Integer points;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "testUser";
        role = Role.ADMIN;
        points = 0;
        userDocument = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .points(0)
                .build();
    }

    @Test
    void userDocumentCreation() {
        assertNotNull(userDocument);
        assertEquals(uuid, userDocument.getUuid());
        assertEquals(username, userDocument.getUsername());
        assertEquals(role, userDocument.getRole());
        assertEquals(points, userDocument.getPoints());
    }

    @Test
    void settersAndGetters() {
        UUID newUuid = UUID.randomUUID();
        String newUsername = "newUser";
        Role newRole = Role.USER;
        Integer newPoints = 0;

        userDocument.setUuid(newUuid);
        userDocument.setUsername(newUsername);
        userDocument.setRole(newRole);
        userDocument.setPoints(newPoints);

        assertEquals(newUuid, userDocument.getUuid());
        assertEquals(newUsername, userDocument.getUsername());
        assertEquals(newRole, userDocument.getRole());
        assertEquals(newPoints, userDocument.getPoints());
    }

    @Test
    void builderPattern() {
        UserDocument user = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .points(points)
                .build();

        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(points, user.getPoints());
    }

    @Test
    void noArgsConstructor() {
        UserDocument emptyUser = new UserDocument();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getUuid());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getRole());
        assertEquals(0, emptyUser.getPoints());
    }

    @Test
    void allArgsConstructor() {
        UserDocument user = new UserDocument(uuid, username, role, points);
        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(points, user.getPoints());
    }

    @Test
    void equalsAndHashCode() {
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, username, role, points);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setUsername("anotherUser");
        assertNotEquals(user1, user2);
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
        UserDocument differentUser = new UserDocument(UUID.randomUUID(), username, role, points);

        assertNotEquals(userDocument, differentUser);
        assertNotEquals(userDocument.hashCode(), differentUser.hashCode());
    }

    @Test
    void equalsAndHashCodeWithDifferentUsernames() {
        UserDocument sameUuidDifferentUsername = new UserDocument(uuid, "differentUser", Role.USER, points);

        assertNotEquals(userDocument, sameUuidDifferentUsername);
        assertNotEquals(userDocument.hashCode(), sameUuidDifferentUsername.hashCode());
    }

    @Test
    void equalsAndHashCodeWithNullFields() {
        UserDocument userWithNullUuid = new UserDocument(null, username, role, points);
        UserDocument userWithNullUsername = new UserDocument(uuid, null, role, points);
        UserDocument userWithNullRole = new UserDocument(uuid, username, null, points);
        UserDocument userWithNullPoints = new UserDocument(uuid, username, role, 0);
        UserDocument completelyNullUser = new UserDocument(null, null, null, 0);

        assertNotEquals(userDocument, userWithNullUuid);
        assertNotEquals(userDocument, userWithNullUsername);
        assertNotEquals(userDocument, userWithNullRole);
        assertEquals(userDocument, userWithNullPoints);
        assertNotEquals(userDocument, completelyNullUser);

        assertNotEquals(userDocument.hashCode(), userWithNullUuid.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullUsername.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullRole.hashCode());
        assertEquals(userDocument.hashCode(), userWithNullPoints.hashCode());
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
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, username, role, points);

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
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, username, role, points);
        UserDocument user3 = new UserDocument(uuid, username, role, points);

        assertEquals(user1, user2);
        assertEquals(user2, user3);
        assertEquals(user1, user3);
    }

    @Test
    void equalsSymmetryTest() {
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, username, role, points);

        assertEquals(user1, user2);
        assertEquals(user2, user1);
    }

    @Test
    void hashCodeEqualityForEqualObjects() {
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, username, role, points);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCodeDifferenceForNonEqualObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "user1", Role.ADMIN, points);
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "user2", Role.ADMIN, points);

        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithNullAttributes() {
        UserDocument user1 = new UserDocument(null, null, null, 0);
        UserDocument user2 = new UserDocument(null, null, null, 0);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUuid() {
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(null, username, role, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUsername() {
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, null, role, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullRole() {
        UserDocument user1 = new UserDocument(uuid, username, role, points);
        UserDocument user2 = new UserDocument(uuid, username, null, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toStringHandlesNullValues() {
        UserDocument user = new UserDocument(null, null, null, 0);
        String s = user.toString();

        assertNotNull(s);
        assertTrue(s.contains("UserDocument"));
    }


    @Test
    void builderHandlesNullValues() {
        UserDocument user = UserDocument.builder()
                .uuid(null)
                .username(null)
                .role(null)
                .points(0)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertEquals(0, user.getPoints());
    }

    @Test
    void settersHandleNullValues() {
        userDocument.setUuid(null);
        userDocument.setUsername(null);
        userDocument.setRole(null);
        userDocument.setPoints(null);

        assertNull(userDocument.getUuid());
        assertNull(userDocument.getUsername());
        assertNull(userDocument.getRole());
        assertNull(userDocument.getPoints());
    }

    @Test
    void hashCodeDifferentForDifferentObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "UserA", Role.ADMIN, points);
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "UserB", Role.ADMIN, points);

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
        assertNull(user.getRole());
    }

    @Test
    void builderHandlesOnlyUsername() {
        UserDocument user = UserDocument.builder()
                .username(username)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertEquals(username, user.getUsername());
        assertNull(user.getRole());
    }

    @Test
    void builderHandlesOnlyRole() {
        UserDocument user = UserDocument.builder()
                .role(role)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertEquals(role, user.getRole());
    }


    @Test
    void builderHandlesOnlyPoints() {
        UserDocument user = UserDocument.builder()
                .points(points)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertEquals(points, user.getPoints());
    }

    @Test
    void builderCreatesNewInstances() {
        UserDocument user1 = UserDocument.builder().uuid(uuid).username(username).role(role)
                .build();

        UserDocument user2 = UserDocument.builder().uuid(uuid).username(username).role(role)
                .build();

        assertNotSame(user1, user2);
        assertEquals(user1, user2);
    }

    @Test
    void builderWithoutParametersCreatesValidObject() {
        UserDocument user = UserDocument.builder().build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
    }

    @Test
    void modifyingBuiltObjectDoesNotAffectOriginalBuilder() {
        UserDocument.UserDocumentBuilder builder = UserDocument.builder().uuid(uuid).username(username).role(role);

        UserDocument user1 = builder.build();
        UserDocument user2 = builder.uuid(UUID.randomUUID()).username("newUser").role(Role.USER)
                .build();

        assertNotEquals(user1, user2);
        assertNotEquals(user1.getUuid(), user2.getUuid());
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getRole(), user2.getRole());
    }
}
