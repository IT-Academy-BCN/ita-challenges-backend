package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        Solution solution = new Solution(uuid, null);
        assertEquals(uuid, solution.getUuid());
    }

    @Test
    void getSolutionText(){

        String solutionText = "Test example.";
        Solution solution = new Solution(null, solutionText);
        assertEquals(solutionText, solution.getSolutionText());
    }
}
