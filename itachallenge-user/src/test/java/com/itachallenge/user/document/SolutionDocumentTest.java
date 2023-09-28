package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;


class SolutionDocumentTest {

    UUID uuid = UUID.randomUUID();
    String solutionText = "Ipsum...";
    SolutionDocument solutionDocument = new SolutionDocument(uuid, solutionText);

    @Test
    void getUuid(){
        assertEquals(uuid, solutionDocument.getUuid());
    }
    @Test
    void getSolutionText(){
        assertEquals(solutionText, solutionDocument.getSolutionText());
    }

}
