package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class SolutionAttemptDocumentTest {

    UUID uuid = UUID.randomUUID();
    String solutionText = "Ipsum...";
    SolutionAttemptDocument solutionAttemptDocument = new SolutionAttemptDocument(uuid, solutionText);
    SolutionAttemptDocument noArgsSolutionAttemptDocument = new SolutionAttemptDocument();

    @Test
    void getUuid_test(){
        assertEquals(uuid, solutionAttemptDocument.getUuid());
    }

    @Test
    void getSolutionText_test(){
        assertEquals(solutionText, solutionAttemptDocument.getSolutionText());
    }

    @Test
    void noArgsBuilder_test(){
        assertNotNull(noArgsSolutionAttemptDocument);
    }

    @Test
    void setSolutionDocument_test(){
        noArgsSolutionAttemptDocument.setUuid(uuid);
        noArgsSolutionAttemptDocument.setSolutionText(solutionText);
        assertEquals(solutionText, noArgsSolutionAttemptDocument.getSolutionText());
        assertEquals(uuid, noArgsSolutionAttemptDocument.getUuid());
    }
}


