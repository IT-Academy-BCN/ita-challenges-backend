package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSolutionsTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        UserSolutions userSolutions = new UserSolutions(uuid, null, null, null, false, null, 0, null);
        assertEquals(uuid, userSolutions.getUuid());
    }

    @Test
    void getUserId() {
        UUID userId = UUID.randomUUID();
        UserSolutions userSolutions = new UserSolutions(null, userId, null, null, false, null, 0, null);
        assertEquals(userId, userSolutions.getUserId());
    }

    @Test
    void getChallengeId() {
        UUID challengeId = UUID.randomUUID();
        UserSolutions userSolutions = new UserSolutions(null, null, challengeId, null, false, null, 0, null);
        assertEquals(challengeId, userSolutions.getChallengeId());
    }

    @Test
    void getLanguageId() {
        UUID languageId = UUID.randomUUID();
        UserSolutions userSolutions = new UserSolutions(null, null, null, languageId, false, null, 0, null);
        assertEquals(languageId, userSolutions.getLanguageId());
    }

    @Test
    void getBookmarked() {
        boolean bookmarked = true;
        UserSolutions userSolutions = new UserSolutions(null, null, null, null, bookmarked, null, 0, null);
        assertEquals(bookmarked, userSolutions.isBookmarked());
    }

    @Test
    void getStatus() {
        String status = "ended";
        UserSolutions userSolutions = new UserSolutions(null, null, null, null, false, status, 0, null);
        assertEquals(status, userSolutions.getStatus());
    }

    @Test
    void getScore() {
        int score = 95;
        UserSolutions userSolutions = new UserSolutions(null, null, null, null, false, null, score, null);
        assertEquals(score, userSolutions.getScore());
    }

    @Test
    void getSolutions() {
        Solution[] solutions = new Solution[3];
        solutions[0] = Solution.builder()
                .uuid(UUID.randomUUID())
                .solutionText("Example Test")
                .build();
        UserSolutions userSolutions = new UserSolutions(null, null, null, null, false, null, 0, solutions);
        assertEquals(solutions, userSolutions.getSolution());
        assertEquals(solutions.length, userSolutions.getSolution().length);
    }
}
