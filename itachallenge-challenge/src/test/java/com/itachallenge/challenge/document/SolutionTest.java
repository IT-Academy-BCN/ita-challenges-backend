package com.itachallenge.challenge.document;


import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        SolutionDocument solution = new SolutionDocument(uuid, null, 0);
        assertEquals(uuid, solution.getUuid());
    }

    @Test
    void getSolutionText() {
        String solutionText = "Solution Text";
        SolutionDocument solution = new SolutionDocument(null, solutionText, 0);
        assertEquals(solutionText, solution.getSolutionText());
    }

    @Test
    void getIdLanguage() {
        int idLanguage = 1;
        SolutionDocument solution = new SolutionDocument(null, null, idLanguage);
        assertEquals(idLanguage, solution.getIdLanguage());
    }
}
