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
    private Set<UUID> bookmarkChallenges;
    private UUID favoriteChallenge;
    private UUID bookmarkChallenge;
    private Integer points;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "testUser";
        role = Role.ADMIN;
        favoriteChallenges = new HashSet<>();
        favoriteChallenge = UUID.randomUUID();
        favoriteChallenges.add(favoriteChallenge);
        bookmarkChallenges = new HashSet<>();
        bookmarkChallenge = UUID.randomUUID();
        bookmarkChallenges.add(bookmarkChallenge);
        points = 0;
        userDocument = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .favoriteChallenges(favoriteChallenges)
                .bookmarkChallenges(bookmarkChallenges)
                .points(points)
                .build();
    }

    @Test
    void userDocumentCreation() {
        assertNotNull(userDocument);
        assertEquals(uuid, userDocument.getUuid());
        assertEquals(username, userDocument.getUsername());
        assertEquals(role, userDocument.getRole());
        assertEquals(favoriteChallenges, userDocument.getFavoriteChallenges());
        assertEquals(bookmarkChallenges, userDocument.getBookmarkChallenges());
        assertEquals(points, userDocument.getPoints());
    }

    @Test
    void settersAndGetters() {
        UUID newUuid = UUID.randomUUID();
        String newUsername = "newUser";
        Role newRole = Role.USER;
        Set<UUID> newFavoriteChallenges = Set.of(UUID.randomUUID());
        Set<UUID> newBookmarkChallenges = Set.of(UUID.randomUUID());
        Integer newPoints = 0;

        userDocument.setUuid(newUuid);
        userDocument.setUsername(newUsername);
        userDocument.setRole(newRole);
        userDocument.setFavoriteChallenges(newFavoriteChallenges);
        userDocument.setBookmarkChallenges(newBookmarkChallenges);
        userDocument.setPoints(newPoints);

        assertEquals(newUuid, userDocument.getUuid());
        assertEquals(newUsername, userDocument.getUsername());
        assertEquals(newRole, userDocument.getRole());
        assertEquals(newFavoriteChallenges, userDocument.getFavoriteChallenges());
        assertEquals(newBookmarkChallenges, userDocument.getBookmarkChallenges());
        assertEquals(newPoints, userDocument.getPoints());
    }

    @Test
    void builderPattern() {
        UserDocument user = UserDocument.builder()
                .uuid(uuid)
                .username(username)
                .role(role)
                .favoriteChallenges(favoriteChallenges)
                .bookmarkChallenges(bookmarkChallenges)
                .points(points)
                .build();

        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(favoriteChallenges, user.getFavoriteChallenges());
        assertEquals(bookmarkChallenges, user.getBookmarkChallenges());
        assertEquals(points, user.getPoints());
    }

    @Test
    void noArgsConstructor() {
        UserDocument emptyUser = new UserDocument();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getUuid());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getRole());
        assertNull(emptyUser.getFavoriteChallenges());
        assertNull(emptyUser.getBookmarkChallenges());
        assertEquals(0, emptyUser.getPoints());
    }

    @Test
    void allArgsConstructor() {
        UserDocument user = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        assertNotNull(user);
        assertEquals(uuid, user.getUuid());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(favoriteChallenges, user.getFavoriteChallenges());
        assertEquals(bookmarkChallenges, user.getBookmarkChallenges());
        assertEquals(points, user.getPoints());
    }

    @Test
    void equalsAndHashCode() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);

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
        assertTrue(toString.contains(bookmarkChallenge.toString()), "ToString should contain bookmark challenge");
        assertTrue(toString.contains(points.toString()), "ToString should contain points");
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
        UserDocument differentUser = new UserDocument(UUID.randomUUID(), username, role, favoriteChallenges, bookmarkChallenges, points);

        assertNotEquals(userDocument, differentUser);
        assertNotEquals(userDocument.hashCode(), differentUser.hashCode());
    }

    @Test
    void equalsAndHashCodeWithDifferentUsernames() {
        UserDocument sameUuidDifferentUsername = new UserDocument(uuid, "differentUser", Role.USER, favoriteChallenges, bookmarkChallenges, points);

        assertNotEquals(userDocument, sameUuidDifferentUsername);
        assertNotEquals(userDocument.hashCode(), sameUuidDifferentUsername.hashCode());
    }

    @Test
    void equalsAndHashCodeWithNullFields() {
        UserDocument userWithNullUuid = new UserDocument(null, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument userWithNullUsername = new UserDocument(uuid, null, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument userWithNullRole = new UserDocument(uuid, username, null, favoriteChallenges, bookmarkChallenges, points);
        UserDocument userWithNullFavoriteChallenges = new UserDocument(uuid, username, role, null, bookmarkChallenges, points);
        UserDocument userWithNullBookmarkChallenges = new UserDocument(uuid, username, role, favoriteChallenges, null, points);
        UserDocument userWithNullPoints = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, null);
        UserDocument completelyNullUser = new UserDocument(null, null, null, null, null, null);

        assertNotEquals(userDocument, userWithNullUuid);
        assertNotEquals(userDocument, userWithNullUsername);
        assertNotEquals(userDocument, userWithNullRole);
        assertNotEquals(userDocument, userWithNullFavoriteChallenges);
        assertNotEquals(userDocument, userWithNullBookmarkChallenges);
        assertNotEquals(userDocument, userWithNullPoints);
        assertNotEquals(userDocument, completelyNullUser);

        assertNotEquals(userDocument.hashCode(), userWithNullUuid.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullUsername.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullRole.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullFavoriteChallenges.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullBookmarkChallenges.hashCode());
        assertNotEquals(userDocument.hashCode(), userWithNullPoints.hashCode());
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
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);

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
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user3 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);

        assertEquals(user1, user2);
        assertEquals(user2, user3);
        assertEquals(user1, user3);
    }

    @Test
    void equalsSymmetryTest() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);

        assertEquals(user1, user2);
        assertEquals(user2, user1);
    }

    @Test
    void hashCodeEqualityForEqualObjects() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCodeDifferenceForNonEqualObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "user1", Role.ADMIN, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "user2", Role.ADMIN, favoriteChallenges, bookmarkChallenges, points);

        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithNullAttributes() {
        UserDocument user1 = new UserDocument(null, null, null, null, null, null);
        UserDocument user2 = new UserDocument(null, null, null, null, null, null);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUuid() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(null, username, role, favoriteChallenges, bookmarkChallenges, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullUsername() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, null, role, favoriteChallenges, bookmarkChallenges, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullRole() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, null, favoriteChallenges, bookmarkChallenges, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullFavoriteChallenges() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, null, bookmarkChallenges, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsWithOneNullBookmarkChallenges() {
        UserDocument user1 = new UserDocument(uuid, username, role, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(uuid, username, role, favoriteChallenges, null, points);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toStringHandlesNullValues() {
        UserDocument user = new UserDocument(null, null, null, null, null, null);
        String toString = user.toString();

        assertTrue(toString.contains("UserDocument"), "ToString should contain class name");
        assertFalse(toString.contains("uuid="), "ToString should not contain 'uuid=' when null");
        assertFalse(toString.contains("username="), "ToString should not contain 'username=' when null");
        assertFalse(toString.contains("role="), "ToString should not contain 'role=' when null");
        assertFalse(toString.contains("favoriteChallenges="), "ToString should not contain 'favoriteChallenges=' when null");
        assertFalse(toString.contains("bookmarkChallenges="), "ToString should not contain 'bookmarkChallenges=' when null");
        assertFalse(toString.contains("points="), "ToString should not contain 'points=' when null");
        assertEquals("UserDocument{}", toString, "ToString should return an empty object representation");
    }


    @Test
    void builderHandlesNullValues() {
        UserDocument user = UserDocument.builder()
                .uuid(null)
                .username(null)
                .role(null)
                .favoriteChallenges(null)
                .bookmarkChallenges(null)
                .points(null)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertNull(user.getFavoriteChallenges());
        assertNull(user.getBookmarkChallenges());
        assertNull(user.getPoints());
    }

    @Test
    void settersHandleNullValues() {
        userDocument.setUuid(null);
        userDocument.setUsername(null);
        userDocument.setRole(null);
        userDocument.setFavoriteChallenges(null);
        userDocument.setBookmarkChallenges(null);
        userDocument.setPoints(null);

        assertNull(userDocument.getUuid());
        assertNull(userDocument.getUsername());
        assertNull(userDocument.getRole());
        assertNull(userDocument.getFavoriteChallenges());
        assertNull(userDocument.getBookmarkChallenges());
        assertNull(userDocument.getPoints());
    }

    @Test
    void hashCodeDifferentForDifferentObjects() {
        UserDocument user1 = new UserDocument(UUID.randomUUID(), "UserA", Role.ADMIN, favoriteChallenges, bookmarkChallenges, points);
        UserDocument user2 = new UserDocument(UUID.randomUUID(), "UserB", Role.ADMIN, favoriteChallenges, bookmarkChallenges, points);

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
    void builderHandlesOnlyBookmarkChallenges() {
        UserDocument user = UserDocument.builder()
                .bookmarkChallenges(bookmarkChallenges)
                .build();

        assertNotNull(user);
        assertNull(user.getUuid());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertEquals(bookmarkChallenges, user.getBookmarkChallenges());
    }

    @Test
    void builderCreatesNewInstances() {
        UserDocument user1 = UserDocument.builder().uuid(uuid).username(username).role(role)
                .favoriteChallenges(favoriteChallenges)
                .bookmarkChallenges(bookmarkChallenges)
                .build();

        UserDocument user2 = UserDocument.builder().uuid(uuid).username(username).role(role)
                .favoriteChallenges(favoriteChallenges)
                .bookmarkChallenges(bookmarkChallenges)
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
        assertNull(user.getFavoriteChallenges());
        assertNull(user.getBookmarkChallenges());
    }

    @Test
    void modifyingBuiltObjectDoesNotAffectOriginalBuilder() {
        UserDocument.UserDocumentBuilder builder = UserDocument.builder().uuid(uuid).username(username).role(role)
                .favoriteChallenges(favoriteChallenges)
                .bookmarkChallenges(bookmarkChallenges);

        UserDocument user1 = builder.build();
        UserDocument user2 = builder.uuid(UUID.randomUUID()).username("newUser").role(Role.USER)
                .favoriteChallenges(new HashSet<>())
                .bookmarkChallenges(new HashSet<>())
                .build();

        assertNotEquals(user1, user2);
        assertNotEquals(user1.getUuid(), user2.getUuid());
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getRole(), user2.getRole());
        assertNotEquals(user1.getFavoriteChallenges(), user2.getFavoriteChallenges());
        assertNotEquals(user1.getBookmarkChallenges(), user2.getBookmarkChallenges());
    }

}

