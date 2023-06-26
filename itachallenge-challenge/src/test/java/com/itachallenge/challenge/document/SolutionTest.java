package com.itachallenge.challenge.document;


import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        Solution solution = new Solution(uuid, null, 0);
        assertEquals(uuid, solution.getUuid());
    }

    @Test
    void getSolutionText() {
        String solutionText = "Solution Text";
        Solution solution = new Solution(null, solutionText, 0);
        assertEquals(solutionText, solution.getSolutionText());
    }

    @Test
    void getIdLanguage() {
        int idLanguage = 1;
        Solution solution = new Solution(null, null, idLanguage);
        assertEquals(idLanguage, solution.getIdLanguage());
    }
}
