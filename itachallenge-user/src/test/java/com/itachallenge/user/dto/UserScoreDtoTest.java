package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserScoreDtoTest {

    private UUID challengeId1 = UUID.randomUUID();
    private UUID languageId1 = UUID.randomUUID();
    private UUID userId1 = UUID.randomUUID();
    private UUID solutionId1 = UUID.randomUUID();
    private UUID solutionId2 = UUID.randomUUID();
    private String solutionText1 = "Solution number 1";
    private String solutionText2 = "Solution number 2";
    private int solutionSize1 = 2;
    SolutionDocument SolutionDocument1 = new SolutionDocument(solutionId1,solutionText1,languageId1);
    SolutionDocument SolutionDocument2 = new SolutionDocument(solutionId2,solutionText2,languageId1);
    private List<SolutionDocument> solutions1 = List.of(SolutionDocument1,SolutionDocument2);

    private UserScoreDto userScoreDto1 = UserScoreDto.builder()
            .challengeId(challengeId1)
            .languageID(languageId1)
            .userId(userId1)
            .solutions(solutions1)
            .solutionsSize(solutionSize1)
            .build();

    @Test
    void getChallengeId(){
        assertEquals(challengeId1, userScoreDto1.getChallengeId());
    }

    @Test
    void getLanguageId(){
        assertEquals(languageId1, userScoreDto1.getLanguageID());
    }
    @Test
    void getUserId(){
        assertEquals(userId1, userScoreDto1.getUserId());
    }
    @Test
    void getSolutions(){
        assertEquals(solutions1, userScoreDto1.getSolutions());
    }
    @Test
    void getSolutionSize(){
        assertEquals(solutionSize1, userScoreDto1.getSolutionsSize());
    }

}
