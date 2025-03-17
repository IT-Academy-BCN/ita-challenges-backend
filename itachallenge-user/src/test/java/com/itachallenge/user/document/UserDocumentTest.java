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
    private Set<UUID> favoriteChallenges;
    private UUID favoriteChallenge;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "testUser";
        role = Role.ADMIN;
        favoriteChallenges = new HashSet<>();
        favoriteChallenge = UUID.randomUUID();
        favoriteChallenges.add(favoriteChallenge);
        userDocument = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .favoriteChallenges(favoriteChallenges)
                .build();
    }

    @Test
    void userDocumentCreation() {
        assertNotNull(userDocument);
        assertEquals(uuid, userDocument.getUuid());
        assertEquals(username, userDocument.getUsername());
        assertEquals(role, userDocument.getRole());
        assertEquals(favoriteChallenges, userDocument.getFavoriteChallenges());
    }

    @Test
    void settersAndGetters() {
        UUID newUuid = UUID.randomUUID();
        String newUsername = "newUser";
        Role newRole = Role.USER;
        Set<UUID> newFavoriteChallenges = Set.of(UUID.randomUUID());

        userDocument.setUuid(newUuid);
        userDocument.setUsername(newUsername);
        userDocument.setRole(newRole);
        userDocument.setFavoriteChallenges(newFavoriteChallenges);

        assertEquals(newUuid, userDocument.getUuid());
        assertEquals(newUsername, userDocument.getUsername());
        assertEquals(newRole, userDocument.getRole());
        assertEquals(newFavoriteChallenges, userDocument.getFavoriteChallenges());
    }

    @Test
    void builderPattern() {
        UserDocument user = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .favoriteChallenges(favoriteChallenges)
                .build();

        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(favoriteChallenges, user.getFavoriteChallenges());
    }

    @Test
    void noArgsConstructor() {
        UserDocument emptyUser = new UserDocument();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getUuid());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getRole());
        assertNull(emptyUser.getFavoriteChallenges());
    }

    @Test
    void allArgsConstructor() {
        UserDocument user = new UserDocument(uuid, username, role, favoriteChallenges);
        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(favoriteChallenges, user.getFavoriteChallenges());
    }

    @Test
    void equalsAndHashCode() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges);

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
        assertTrue(toString.contains(role.toString()), "ToString should contain role");
        assertTrue(toString.contains(favoriteChallenge.toString()), "ToString should contain favorite challenge");
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
        UserDocument differentUser = new UserDocument(UUID.randomUUID(), username, role, favoriteChallenges);

        assertNotEquals(userDocument, differentUser);
        assertNotEquals(userDocument.hashCode(), differentUser.hashCode());
    }

    @Test
    void equalsAndHashCodeWithDifferentUsernames() {
        UserDocument sameUuidDifferentUsername = new UserDocument(uuid, "differentUser", Role.USER, favoriteChallenges);

        assertNotEquals(userDocument, sameUuidDifferentUsername);
        assertNotEquals(userDocument.hashCode(), sameUuidDifferentUsername.hashCode());
    }

    @Test
    void equalsAndHashCodeWithNullFields() {
        UserDocument userWithNullUuid = new UserDocument(null, username, role, favoriteChallenges);
        UserDocument userWithNullUsername = new UserDocument(uuid, null, role, favoriteChallenges);
        UserDocument userWithNullRole = new UserDocument(uuid, username, null, favoriteChallenges);
        UserDocument userWithNullFavoriteChallenges = new UserDocument(uuid, username, role, null);
        UserDocument completelyNullUser = new UserDocument(null, null, null, null);

        assertNotEquals(userDocument, userWithNullUuid);
        assertNotEquals(userDocument, userWithNullUsername);
        assertNotEquals(userDocument, userWithNullRole);
        assertNotEquals(userDocument, userWithNullFavoriteChallenges);
        assertNotEquals(userDocument, completelyNullUser);

        assertNotEquals(userDocument.hashCode(), userWithNullUuid.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullUsername.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullRole.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullFavoriteChallenges.hashCode());
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
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges);

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
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user3 = new UserDocument(uuid, username, role, favoriteChallenges);

        assertEquals(user1, user2);
        assertEquals(user2, user3);
        assertEquals(user1, user3);
    }

    @Test
    void equalsSymmetryTest() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges);

        assertEquals(user1, user2);
        assertEquals(user2, user1);
    }

    @Test
    void hashCodeEqualityForEqualObjects() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCodeDifferenceForNonEqualObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "user1", Role.ADMIN, favoriteChallenges);
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "user2", Role.ADMIN, favoriteChallenges);

        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithNullAttributes() {
        UserDocument user1 = new UserDocument(null, null, null, null);
        UserDocument user2 = new UserDocument(null, null, null, null);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUuid() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(null, username, role, favoriteChallenges);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUsername() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, null, role, favoriteChallenges);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullRole() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, null, favoriteChallenges);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullFavoriteChallenges() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges);
        UserDocument user2 = new UserDocument(uuid, username, role, null);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }


    @Test
    void toStringHandlesNullValues() {
        UserDocument user = new UserDocument(null, null, null, null);
        String toString = user.toString();

        assertTrue(toString.contains("UserDocument"), "ToString should contain class name");
        assertFalse(toString.contains("uuid="), "ToString should not contain 'uuid=' when null");
        assertFalse(toString.contains("username="), "ToString should not contain 'username=' when null");
        assertFalse(toString.contains("role="), "ToString should not contain 'role=' when null");
        assertFalse(toString.contains("favoriteChallenges="), "ToString should not contain 'favoriteChallenges=' when null");
        assertEquals("UserDocument{}", toString, "ToString should return an empty object representation");
    }


    @Test
    void builderHandlesNullValues() {
        UserDocument user = UserDocument.builder()
                .uuid(null)
                .username(null)
                .role(null)
                .favoriteChallenges(null)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertNull(user.getFavoriteChallenges());
    }

    @Test
    void settersHandleNullValues() {
        userDocument.setUuid(null);
        userDocument.setUsername(null);
        userDocument.setRole(null);
        userDocument.setFavoriteChallenges(null);

        assertNull(userDocument.getUuid());
        assertNull(userDocument.getUsername());
        assertNull(userDocument.getRole());
        assertNull(userDocument.getFavoriteChallenges());
    }

    @Test
    void hashCodeDifferentForDifferentObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "UserA", Role.ADMIN, favoriteChallenges);
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "UserB", Role.ADMIN, favoriteChallenges);

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
    void builderHandlesOnlyFavoriteChallenges() {
        UserDocument user = UserDocument.builder()
                .favoriteChallenges(favoriteChallenges)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertEquals(favoriteChallenges, user.getFavoriteChallenges());
    }

    @Test
    void builderCreatesNewInstances() {
        UserDocument user1 = UserDocument.builder().uuid(uuid).username(username).role(role)
                .favoriteChallenges(favoriteChallenges).build();
        UserDocument user2 = UserDocument.builder().uuid(uuid).username(username).role(role)
                .favoriteChallenges(favoriteChallenges).build();

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
        assertNull(user.getFavoriteChallenges());
    }

    @Test
    void modifyingBuiltObjectDoesNotAffectOriginalBuilder() {
        UserDocument.UserDocumentBuilder builder = UserDocument.builder().uuid(uuid).username(username).role(role)
                .favoriteChallenges(favoriteChallenges);

        UserDocument user1 = builder.build();
        UserDocument user2 = builder.uuid(UUID.randomUUID()).username("newUser").role(Role.USER)
                .favoriteChallenges(new HashSet<>()).build();

        assertNotEquals(user1, user2);
        assertNotEquals(user1.getUuid(), user2.getUuid());
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getRole(), user2.getRole());
        assertNotEquals(user1.getFavoriteChallenges(), user2.getFavoriteChallenges());
    }

}

