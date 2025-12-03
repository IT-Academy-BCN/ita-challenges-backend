    package com.itachallenge.userinteraction.document.bookmark;

    import java.time.LocalDateTime;
    import java.util.UUID;

    import org.junit.jupiter.api.Test;

    import static org.junit.jupiter.api.Assertions.*;

    class BookmarkDocumentTest {

@Test
void equalsHashCode_fullBranchCoverage() {
    UUID uuid = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID challengeId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();

    BookmarkDocument a = BookmarkDocument.builder()
            .uuid(uuid)
            .userId(userId)
            .challengeId(challengeId)
            .createdAt(createdAt)
            .build();

    BookmarkDocument b = BookmarkDocument.builder()
            .uuid(uuid)
            .userId(userId)
            .challengeId(challengeId)
            .createdAt(createdAt)
            .build();

    BookmarkDocument c = BookmarkDocument.builder()
            .uuid(UUID.randomUUID())
            .userId(userId)
            .challengeId(challengeId)
            .createdAt(createdAt)
            .build();

    assertEquals(a, b);
    assertNotEquals(a, c);

    // equals — (Lombok "this == o")
    assertEquals(a, a);

    // equals — null (Lombok "o == null")
    assertNotEquals(null, a);

    // equals — different type (Lombok "getClass() != o.getClass()")
    assertNotEquals("some string", a);

    assertEquals(a.hashCode(), b.hashCode());

    BookmarkDocument empty = new BookmarkDocument();
    assertDoesNotThrow(empty::hashCode);

    assertNotNull(a.toString());

    BookmarkDocument n1 = new BookmarkDocument();
    BookmarkDocument n2 = new BookmarkDocument();

    assertEquals(n1, n2);
    assertEquals(n1.hashCode(), n2.hashCode());
}
    }
