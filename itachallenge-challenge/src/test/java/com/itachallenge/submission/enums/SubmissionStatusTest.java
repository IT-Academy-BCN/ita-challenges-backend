package com.itachallenge.submission.enums;

import com.itachallenge.challenge.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionStatusTest {

    @DisplayName("Should return SUBMITTED_COMPLETE status ignoring case")
    @ParameterizedTest
    @ValueSource(strings = {
            "SUBMITTED_COMPLETE", "submitted_complete", "Submitted_Complete"
    })
    void shouldReturnSubmittedCompleteIgnoringCase(String input) {
        // When
        SubmissionStatus result = SubmissionStatus.fromString(input);

        // Then
        assertEquals(SubmissionStatus.SUBMITTED_COMPLETE, result);
    }

    @DisplayName("Should return IN_PROGRESS status ignoring case")
    @ParameterizedTest
    @ValueSource(strings = {
            "IN_PROGRESS", "in_progress", "In_Progress"
    })
    void shouldReturnInProgressIgnoringCase(String input) {
        // When
        SubmissionStatus result = SubmissionStatus.fromString(input);

        // Then
        assertEquals(SubmissionStatus.IN_PROGRESS, result);
    }

    @DisplayName("Should return SUBMITTED_INCOMPLETE status ignoring case")
    @ParameterizedTest
    @ValueSource(strings = {
            "SUBMITTED_INCOMPLETE", "submitted_incomplete", "Submitted_Incomplete"
    })
    void shouldReturnSubmittedIncompleteIgnoringCase(String input) {
        // When
        SubmissionStatus result = SubmissionStatus.fromString(input);

        // Then
        assertEquals(SubmissionStatus.SUBMITTED_INCOMPLETE, result);
    }

    @DisplayName("Should return null when input status string is null")
    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThrows(BadRequestException.class, () ->
                SubmissionStatus.fromString(null)
        );
    }

    @DisplayName("Should return null when status value does not exist")
    @Test
    void shouldReturnNullWhenValueDoesNotExist() {
        String input = "UNKNOWN_STATUS";

        assertThrows(BadRequestException.class, () ->
                SubmissionStatus.fromString(input)
        );
    }
}
