package com.itachallenge.challenge.document;

import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeTest {
    List<UUID> tags = List.of(UUID.randomUUID());

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        ChallengeDocument challenge = new ChallengeDocument(uuid,
                null,
                null,
                null,
                null,
                null,
                null,
                Topic.LISTS,
                null,
                tags);
        assertEquals(uuid, challenge.getUuid());
    }

    @Test
    void getTitle() {
        String expectedTitle = "Test challenge";
        ChallengeDocument challenge = new ChallengeDocument(null,
                expectedTitle,
                null,
                null,
                null,
                null,
                null,
                Topic.COMPONENTS,
                null,
                tags);
        assertEquals(expectedTitle, challenge.getTitle());
    }

    @Test
    void getLevel() {
        String level = "Intermediate";
        ChallengeDocument challenge = new ChallengeDocument(null,
                null,
                level,
                null,
                null,
                null,
                null,
                Topic.COMPONENTS,
                null,
                tags);
        assertEquals(level, challenge.getLevel());
    }

    @Test
    void getCreationDate() {
        LocalDateTime creationDate = now();
        ChallengeDocument challenge = new ChallengeDocument(null,
                null,
                null,
                creationDate,
                null,
                null,
                null,
                Topic.COMPONENTS,
                null,
                tags);
        assertTrue(creationDate.truncatedTo(ChronoUnit.SECONDS).isEqual(challenge.getCreationDate().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    void getDetail() {
        DetailDocument detail = new DetailDocument(null);
        ChallengeDocument challenge = new ChallengeDocument(null,
                null,
                null,
                null,
                detail,
                null,
                null,
                Topic.COMPONENTS,
                null,
                tags);
        assertEquals(detail, challenge.getDetail());
    }

    @Test
    void getLanguages() {
        UUID uuid = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuid2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        Set<LanguageDocument> languages = Set.of(new LanguageDocument(uuid, "Javascript",
                "https://res.cloudinary.com/itachallenge/image/upload/v1739361249/language_icon_Javascript_asgn04.svg"),
                new LanguageDocument(uuid2, "Python", "https://res.cloudinary.com/itachallenge/image/upload/v1739361249/language_icon_Python_rphody.svg"));

        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, languages, null, Topic.COMPONENTS, null,tags);
        assertEquals(languages, challenge.getLanguages());
    }

    @Test
    void getSolutions() {
        List<UUID> solutions = List.of(UUID.randomUUID(),UUID.randomUUID());

        ChallengeDocument challenge = new ChallengeDocument(null,
                null,
                null,
                null,
                null,
                null,
                solutions,
                Topic.COMPONENTS,
                null,
                tags);
        assertEquals(solutions, challenge.getSolutions());
    }

    @Test
    void getTimesFavorite() {
        int timesFavorite = 20;

        ChallengeDocument challenge = new ChallengeDocument(null,
                null,
                null,
                null,
                null,
                null,
                null,
                Topic.COMPONENTS,
                timesFavorite,
                tags);
        assertEquals(timesFavorite, challenge.getTimesFavorite());
    }

    @Test
    void getTagsTest() {
        UUID uuid = UUID.randomUUID();
        TagDocument tag = new TagDocument(uuid, "POO", "bla bla bla");

        ChallengeDocument challenge = new ChallengeDocument(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Topic.COMPONENTS,
                20,
                List.of(tag.getIdTag()) 
        );

        assertTrue(challenge.getTags().contains(tag.getIdTag()));
        assertEquals(1, challenge.getTags().size());
    }


    @Test
    void setTagsTest() {
        UUID firstTagId = UUID.randomUUID();
        TagDocument firstTag = new TagDocument(firstTagId, "POO", "bla bla bla");

        ChallengeDocument challenge = new ChallengeDocument(
                null, null, null, null, null, null, null,
                Topic.COMPONENTS,
                20,
                new ArrayList<UUID>(List.of(firstTag.getIdTag())) {
                }
        );

        UUID secondTagId = UUID.randomUUID();
        TagDocument secondTag = new TagDocument(secondTagId, "Estructura", "bla bla bla");

        challenge.setTags(secondTag.getIdTag());

        List<UUID> tags = challenge.getTags();
        assertEquals(2, tags.size(), "El challenge debería tener 2 tags");
        assertTrue(tags.contains(firstTagId), "Debe contener el primer tag");
        assertTrue(tags.contains(secondTagId), "Debe contener el nuevo tag");
    }


}