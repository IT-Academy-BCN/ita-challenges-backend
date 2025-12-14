package com.itachallenge.submission.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserChallengeStatusTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "SUBMITTED_COMPLETE", "submitted_complete", "Submitted_Complete"
    })
    void shouldReturnSubmittedCompleteIgnoringCase(String input) {
        // When
        UserChallengeStatus result = UserChallengeStatus.challengeStatusFromString(input);

        // Then
        assertEquals(UserChallengeStatus.SUBMITTED_COMPLETE, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "IN_PROGRESS", "in_progress", "In_Progress"
    })
    void shouldReturnInProgressIgnoringCase(String input) {
        // When
        UserChallengeStatus result = UserChallengeStatus.challengeStatusFromString(input);

        // Then
        assertEquals(UserChallengeStatus.IN_PROGRESS, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SUBMITTED_INCOMPLETE", "submitted_incomplete", "Submitted_Incomplete"
    })
    void shouldReturnSubmittedIncompleteIgnoringCase(String input) {
        // When
        UserChallengeStatus result = UserChallengeStatus.challengeStatusFromString(input);

        // Then
        assertEquals(UserChallengeStatus.SUBMITTED_INCOMPLETE, result);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        // When
        UserChallengeStatus result = UserChallengeStatus.challengeStatusFromString(null);

        // Then
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenValueDoesNotExist() {
        // Given
        String input = "UNKNOWN_STATUS";

        // When
        UserChallengeStatus result = UserChallengeStatus.challengeStatusFromString(input);

        // Then
        assertNull(result);
    }
}

