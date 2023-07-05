package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void getUuid() {
        UUID idSolution = UUID.randomUUID();
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80272");
        SolutionDocument solution = new SolutionDocument(idSolution, null, idLanguage);

        assertEquals(idSolution, solution.getIdSolution());
    }

    @Test
    void getSolutionText() {
        String solutionText = "Solution Text";
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80221");
        SolutionDocument solution = new SolutionDocument(null, solutionText, idLanguage);

        assertEquals(solutionText, solution.getSolutionText());
    }

    @Test
    void getIdLanguage() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80263");
        SolutionDocument solution = new SolutionDocument(null, null, idLanguage);

        assertEquals(idLanguage, solution.getIdLanguage());
    }
}
