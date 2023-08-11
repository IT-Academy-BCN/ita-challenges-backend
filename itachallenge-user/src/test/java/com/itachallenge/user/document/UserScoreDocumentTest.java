package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserScoreDocumentTest {

    private UUID uuid = UUID.randomUUID();
    private UUID userId = UUID.randomUUID();
    private UUID challengeId = UUID.randomUUID();
    private UUID languageId  = UUID.randomUUID();
    private boolean bookmarked = true;
    private int status = 1;
    private int score = 90;
    UUID solutionId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
    UUID solutionId2 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
    String solutionText1 = "Ipsum.. 1";
    String solutionText2 = "Ipsum.. 2";
    SolutionDocument solutionDocument1 = new SolutionDocument(solutionId1, solutionText1,languageId);
    SolutionDocument solutionDocument2 = new SolutionDocument(solutionId2, solutionText2,languageId);
    List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2);
    UserScoreDocument userScoreDocument = new UserScoreDocument(uuid, userId, challengeId, languageId, bookmarked, status,score, solutionDocumentList);

    @Test
    void getUuid(){
        assertEquals(uuid, userScoreDocument.getUuid());
    }
    @Test
    void getUserId(){ assertEquals(userId, userScoreDocument.getUserId());}
    @Test
    void getChallengeId(){
        assertEquals(challengeId, userScoreDocument.getChallengeId());
    }
    @Test
    void getLanguageId(){
        assertEquals(languageId, userScoreDocument.getLanguajeId());
    }
    @Test
    void getBookmark(){
        assertEquals(bookmarked, userScoreDocument.isBookmarked());
    }
    @Test
    void getStatus(){
        assertEquals(status, userScoreDocument.getStatus());
    }
    @Test
    void getScore(){
        assertEquals(score, userScoreDocument.getScore());
    }
    @Test
    void getSolutionDocumentList(){
        assertEquals(solutionDocumentList, userScoreDocument.getSolutionDocument());
    }
}
