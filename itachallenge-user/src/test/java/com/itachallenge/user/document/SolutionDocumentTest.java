package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;


class SolutionDocumentTest {

    UUID uuid = UUID.randomUUID();
    String solutionText = "Ipsum...";
    UUID idLanguage = UUID.randomUUID();
    SolutionDocument solutionDocument = new SolutionDocument(uuid, solutionText,idLanguage);

    @Test
    void getUuid(){
        assertEquals(uuid, solutionDocument.getUuid());
    }
    @Test
    void getSolutionText(){
        assertEquals(solutionText, solutionDocument.getSolutionText());
    }
    @Test
    void getLanguageId(){
        assertEquals(idLanguage, solutionDocument.getIdLanguage());
    }
}
