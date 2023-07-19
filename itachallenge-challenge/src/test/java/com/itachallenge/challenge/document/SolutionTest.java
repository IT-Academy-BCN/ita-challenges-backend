package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        SolutionDocument solution = new SolutionDocument(uuid, null, null);
        assertEquals(uuid, solution.getUuid());
    }

    @Test
    void getSolutionText() {
        String solutionText = "Solution Text";
        SolutionDocument solution = new SolutionDocument(null, solutionText, null);
        assertEquals(solutionText, solution.getSolutionText());
    }

    @Test
    void getIdLanguage() {
        UUID uuidLang = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        SolutionDocument solution = new SolutionDocument(null, null, uuidLang);
        assertEquals(uuidLang, solution.getIdLanguage());
    }
}
