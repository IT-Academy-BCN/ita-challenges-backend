package com.itachallenge.score.document;

import com.itachallenge.score.document.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void testSetAndGetScoreID() {
        UUID expectedScoreID = UUID.randomUUID();
        score.setScoreID(expectedScoreID);
        assertEquals(expectedScoreID, score.getScoreID());
    }

    @Test
    void testSetAndGetUserID() {
        UUID expectedUserID = UUID.randomUUID();
        score.setUserID(expectedUserID);
        assertEquals(expectedUserID, score.getUserID());
    }

    @Test
    void testSetAndGetChallengeID() {
        UUID expectedChallengeID = UUID.randomUUID();
        score.setChallengeID(expectedChallengeID);
        assertEquals(expectedChallengeID, score.getChallengeID());
    }

    @Test
    void testSetAndGetSolutionID() {
        UUID expectedSolutionID = UUID.randomUUID();
        score.setSolutionID(expectedSolutionID);
        assertEquals(expectedSolutionID, score.getSolutionID());
    }

    @Test
    void testScoreInitialization() {
        assertNotNull(score);
        assertNull(score.getScoreID());
        assertNull(score.getUserID());
        assertNull(score.getChallengeID());
        assertNull(score.getSolutionID());
    }
}