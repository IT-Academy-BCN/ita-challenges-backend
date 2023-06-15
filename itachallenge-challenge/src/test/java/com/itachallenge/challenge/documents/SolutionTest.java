package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class SolutionTest {

    @Test
    public void testSolutionClass() {
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
    public void testSolutionMethods() {
        // Crea una instancia de Solution
        Solution solution = Solution.builder()
                .uuid(UUID.randomUUID())
                .solutionText("Solución del problema")
                .idLanguage(1)
                .build();

        // Prueba los métodos getter y setter
        UUID newUuid = UUID.randomUUID();
        solution.setUuid(newUuid);
        assertEquals(newUuid, solution.getUuid());

        String newSolutionText = "Nueva solución";
        solution.setSolutionText(newSolutionText);
        assertEquals(newSolutionText, solution.getSolutionText());

        int newIdLanguage = 2;
        solution.setIdLanguage(newIdLanguage);
        assertEquals(newIdLanguage, solution.getIdLanguage());

        // Prueba el método equals()
        Solution equalSolution = Solution.builder()
                .uuid(newUuid)
                .solutionText(newSolutionText)
                .idLanguage(newIdLanguage)
                .build();
        assertEquals(solution, equalSolution);

        // Prueba el método hashCode()
        assertEquals(solution.hashCode(), equalSolution.hashCode());
    }
}
