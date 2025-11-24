package com.itachallenge.submission.enums;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserSubmissionActionTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldDeserializeSaveIgnoringCase() throws Exception {
        String json = "\"SaVe\"";
        UserSubmissionAction result = mapper.readValue(json, UserSubmissionAction.class);

        assertEquals(UserSubmissionAction.SAVE, result);
    }

    @Test
    void shouldDeserializeGiveUpIgnoringCase() throws Exception {
        String json = "\"GivE_uP\"";
        UserSubmissionAction result = mapper.readValue(json, UserSubmissionAction.class);

        assertEquals(UserSubmissionAction.GIVE_UP, result);
    }

    @Test
    void shouldDeserializeSubmitWithSpaces() throws Exception {
        String json = "\"   submit   \"";
        UserSubmissionAction result = mapper.readValue(json, UserSubmissionAction.class);

        assertEquals(UserSubmissionAction.SUBMIT, result);
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        String json = "\"INVALID_VALUE\"";

        Exception ex = assertThrows(Exception.class, () ->
                mapper.readValue(json, UserSubmissionAction.class)
        );

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Action must be one of"));
    }

    @Test
    void shouldThrowExceptionWhenNull() throws Exception {
        String json = "null";
        UserSubmissionAction result = mapper.readValue(json, UserSubmissionAction.class);

        assertNull(result);
    }
}
