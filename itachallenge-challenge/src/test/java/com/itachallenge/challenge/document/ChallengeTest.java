package com.itachallenge.challenge.document;

import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        ChallengeDocument challenge = new ChallengeDocument(uuid, null, null, null, null, null, null, Topic.LISTS);
        assertEquals(uuid, challenge.getUuid());
    }

    @Test
    void getTitle() {
        Map<Locale, String> titleMap = new HashMap<>();
            titleMap.put(Locale.forLanguageTag("ES"), "Reto de prueba");
            titleMap.put(Locale.forLanguageTag("CA"), "Repte de prova");
            titleMap.put(Locale.ENGLISH, "Test Challenge");
        ChallengeDocument challenge = new ChallengeDocument(null, titleMap, null, null, null, null, null, Topic.COMPONENTS);
        assertEquals(titleMap, challenge.getTitle());
    }

    @Test
    void getLevel() {
        String level = "Intermediate";
        ChallengeDocument challenge = new ChallengeDocument(null, null, level, null, null, null, null, Topic.COMPONENTS);
        assertEquals(level, challenge.getLevel());
    }

    @Test
    void getCreationDate() {
        LocalDateTime creationDate = now();
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, creationDate, null, null, null, Topic.COMPONENTS);
        assertTrue(creationDate.truncatedTo(ChronoUnit.SECONDS).isEqual(challenge.getCreationDate().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    void getDetail() {
        DetailDocument detail = new DetailDocument(null, null, null);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, detail, null, null, Topic.COMPONENTS);
        assertEquals(detail, challenge.getDetail());
    }

    @Test
    void getLanguages() {
        UUID uuid = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuid2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        Set<LanguageDocument> languages = Set.of(new LanguageDocument(uuid, "Javascript"), new LanguageDocument(uuid2, "Python"));

        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, languages, null, Topic.COMPONENTS);
        assertEquals(languages, challenge.getLanguages());
    }

    @Test
    void getSolutions() {
        List<UUID> solutions = List.of(UUID.randomUUID(),UUID.randomUUID());

        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, solutions, Topic.COMPONENTS);
        assertEquals(solutions, challenge.getSolutions());
    }
}