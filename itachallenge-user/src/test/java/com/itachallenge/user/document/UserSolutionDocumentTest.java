package com.itachallenge.user.document;

import com.itachallenge.user.document.enums.ChallengeStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserSolutionDocumentTest {

    private final UUID uuid = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID challengeId = UUID.randomUUID();
    private final UUID languageId  = UUID.randomUUID();
    private final ChallengeStatus challengeStatus = ChallengeStatus.SUBMITTED_COMPLETED;
    UUID solutionId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
    String solutionText1 = "Ipsum.. 1";
    SolutionAttemptDocument solutionAttemptDocument1 = new SolutionAttemptDocument(solutionId1, solutionText1);
    UserSolutionDocument userSolutionDocument = new UserSolutionDocument(uuid, userId, challengeId, languageId, challengeStatus, solutionAttemptDocument1);
    UserSolutionDocument noArgsUserSolutionDocument = new UserSolutionDocument();

    @Test
    void getUuid(){
        assertEquals(uuid, userSolutionDocument.getUuid());
    }

    @Test
    void getUserId(){ assertEquals(userId, userSolutionDocument.getUserId());}

    @Test
    void getChallengeId(){
        assertEquals(challengeId, userSolutionDocument.getChallengeId());
    }

    @Test
    void getLanguageId(){
        assertEquals(languageId, userSolutionDocument.getLanguageId());
    }

    @Test
    void getStatus(){
        assertEquals(challengeStatus, userSolutionDocument.getStatus());
    }

    @Test
    void getSolutionAttemptDocument(){
        assertEquals(solutionAttemptDocument1, userSolutionDocument.getSolutionAttemptDocument());
    }

    @Test
    void noArgsBuilder_test(){
        assertNotNull(noArgsUserSolutionDocument);
    }
}


