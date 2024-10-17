package com.itachallenge.score.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageDTOTest {

    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        messageDTO = new MessageDTO("Initial message");
    }

    @Test
    void testGetMessage() {
        assertEquals("Initial message", messageDTO.getMessage());
    }

    @Test
    void testSetMessage() {
        String newMessage = "New message";
        messageDTO.setMessage(newMessage);
        assertEquals(newMessage, messageDTO.getMessage());
    }

    @Test
    void testMessageDTOInitialization() {
        MessageDTO newMessageDTO = new MessageDTO("Test message");
        assertNotNull(newMessageDTO);
        assertEquals("Test message", newMessageDTO.getMessage());
    }
}