package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class SolutionDocumentTest {

    UUID uuid = UUID.randomUUID();
    String solutionText = "Ipsum...";
    SolutionDocument solutionDocument = new SolutionDocument(uuid, solutionText);
    SolutionDocument noArgsSolutionDocument = new SolutionDocument();

    @Test
    void getUuid_test(){
        assertEquals(uuid, solutionDocument.getUuid());
    }

    @Test
    void getSolutionText_test(){
        assertEquals(solutionText, solutionDocument.getSolutionText());
    }

    @Test
    void noArgsBuilder_test(){
        assertNotNull(noArgsSolutionDocument);
    }

    @Test
    void setSolutionDocument_test(){
        noArgsSolutionDocument.setUuid(uuid);
        noArgsSolutionDocument.setSolutionText(solutionText);
        assertEquals(solutionText, noArgsSolutionDocument.getSolutionText());
        assertEquals(uuid, noArgsSolutionDocument.getUuid());
    }
}


