package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class SolutionTest {

    @Test
    void testSolutionClass() {
        // Crear una instancia de Solution
        UUID uuid = UUID.randomUUID();
        String solutionText = "public class Solution { }";
        int idLanguage = 1;

        Solution solution = new Solution(uuid, solutionText, idLanguage);

        // Verificar los valores de los campos
        assertEquals(uuid, solution.getUuid());
        assertEquals(solutionText, solution.getSolutionText());
        assertEquals(idLanguage, solution.getIdLanguage());

        // Modificar y verificar los campos
        UUID newUuid = UUID.randomUUID();
        String newSolutionText = "public class Solution { }";
        int newIdLanguage = 2;

        solution.setUuid(newUuid);
        solution.setSolutionText(newSolutionText);
        solution.setIdLanguage(newIdLanguage);

        assertEquals(newUuid, solution.getUuid());
        assertEquals(newSolutionText, solution.getSolutionText());
        assertEquals(newIdLanguage, solution.getIdLanguage());
    }

    @Test
    void testGetUuid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Solution solution = new Solution();
        solution.setUuid(uuid);

        // Act
        UUID result = solution.getUuid();

        // Assert
        assertEquals(uuid, result);
    }

    @Test
    void testSetUuid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Solution solution = new Solution();

        // Act
        solution.setUuid(uuid);

        // Assert
        assertEquals(uuid, solution.getUuid());
    }

    @Test
    void testGetSolutionText() {
        // Arrange
        String solutionText = "Test Solution Text";
        Solution solution = new Solution();
        solution.setSolutionText(solutionText);

        // Act
        String result = solution.getSolutionText();

        // Assert
        assertEquals(solutionText, result);
    }

    @Test
    void testSetSolutionText() {
        // Arrange
        String solutionText = "Test Solution Text";
        Solution solution = new Solution();

        // Act
        solution.setSolutionText(solutionText);

        // Assert
        assertEquals(solutionText, solution.getSolutionText());
    }

    @Test
    void testGetIdLanguage() {
        // Arrange
        int idLanguage = 1;
        Solution solution = new Solution();
        solution.setIdLanguage(idLanguage);

        // Act
        int result = solution.getIdLanguage();

        // Assert
        assertEquals(idLanguage, result);
    }

    @Test
    void testSetIdLanguage() {
        // Arrange
        int idLanguage = 1;
        Solution solution = new Solution();

        // Act
        solution.setIdLanguage(idLanguage);

        // Assert
        assertEquals(idLanguage, solution.getIdLanguage());
    }
}
