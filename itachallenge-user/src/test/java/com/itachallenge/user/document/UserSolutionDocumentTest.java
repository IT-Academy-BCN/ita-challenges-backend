package com.itachallenge.user.document;

import com.itachallenge.user.document.enums.ChallengeStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserSolutionDocumentTest {

    private final UUID uuid = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID challengeId = UUID.randomUUID();
    private final UUID languageId  = UUID.randomUUID();
    private final boolean bookmarked = true;
    private final ChallengeStatus challengeStatus = ChallengeStatus.ENDED;
    UUID solutionId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
    UUID solutionId2 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
    String solutionText1 = "Ipsum.. 1";
    String solutionText2 = "Ipsum.. 2";
    SolutionAttemptDocument solutionAttemptDocument1 = new SolutionAttemptDocument(solutionId1, solutionText1);
    SolutionAttemptDocument solutionAttemptDocument2 = new SolutionAttemptDocument(solutionId2, solutionText2);
    List<SolutionAttemptDocument> solutionAttemptDocumentList = List.of(solutionAttemptDocument1, solutionAttemptDocument2);
    UserSolutionDocument userSolutionDocument = new UserSolutionDocument(uuid, userId, challengeId, languageId, bookmarked, challengeStatus, solutionAttemptDocumentList);
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
    void getBookmark(){
        assertEquals(bookmarked, userSolutionDocument.isBookmarked());
    }

    @Test
    void getStatus(){
        assertEquals(challengeStatus, userSolutionDocument.getStatus());
    }

    @Test
    void getSolutionAttemptDocumentList(){
        assertEquals(solutionAttemptDocumentList, userSolutionDocument.getSolutionAttemptDocument());
    }

    @Test
    void noArgsBuilder_test(){
        assertNotNull(noArgsUserSolutionDocument);
    }
}


