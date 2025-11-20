package com.itachallenge.user.document.enums;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolutionActionTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldDeserializeSaveIgnoringCase() throws Exception{
        String json = "\"SaVe\"";
        SolutionAction result = mapper.readValue(json, SolutionAction.class);

        assertEquals(SolutionAction.SAVE, result);
    }

    @Test
    void shouldDeserializeGiveUpIgnoringCase() throws Exception {
        String json = "\"GivE_uP\"";
        SolutionAction result = mapper.readValue(json, SolutionAction.class);

        assertEquals(SolutionAction.GIVE_UP, result);
    }

    @Test
    void shouldDeserializeSubmitWithSpaces() throws Exception {
        String json = "\"   submit   \"";
        SolutionAction result = mapper.readValue(json, SolutionAction.class);

        assertEquals(SolutionAction.SUBMIT, result);
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        String json = "\"INVALID_VALUE\"";

        Exception ex = assertThrows(Exception.class, () ->
                mapper.readValue(json, SolutionAction.class)
        );

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Action must be one of"));
    }

    @Test
    void shouldThrowExceptionWhenNull() throws Exception{
        String json = "null";
        SolutionAction result = mapper.readValue(json, SolutionAction.class);

        assertNull(result);
    }
}
