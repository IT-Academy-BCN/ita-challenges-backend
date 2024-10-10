package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        String solutionText = "Solution Text";
        UUID uuidLang = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID idChallenge = UUID.randomUUID();
        SolutionDocument solution = new SolutionDocument(uuid, solutionText, uuidLang, idChallenge);
        assertEquals(uuid, solution.getUuid());
    }

    @Test
    void getSolutionText() {
        UUID uuid = UUID.randomUUID();
        String solutionText = "Solution Text";
        UUID uuidLang = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID idChallenge = UUID.randomUUID();
        SolutionDocument solution = new SolutionDocument(uuid, solutionText, uuidLang, idChallenge);
        assertEquals(solutionText, solution.getSolutionText());
    }

    @Test
    void getIdLanguage() {
        UUID uuid = UUID.randomUUID();
        String solutionText = "Solution Text";
        UUID uuidLang = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID idChallenge = UUID.randomUUID();
        SolutionDocument solution = new SolutionDocument(uuid, solutionText, uuidLang, idChallenge);
        assertEquals(uuidLang, solution.getIdLanguage());
    }

    @Test
    void getIdChallenge() {
        UUID uuid = UUID.randomUUID();
        String solutionText = "Solution Text";
        UUID uuidLang = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID idChallenge = UUID.randomUUID();
        SolutionDocument solution = new SolutionDocument(uuid, solutionText, uuidLang, idChallenge);
        assertEquals(idChallenge, solution.getIdChallenge());
    }
}
