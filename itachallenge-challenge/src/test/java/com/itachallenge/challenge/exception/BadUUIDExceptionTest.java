package com.itachallenge.challenge.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BadUUIDExceptionTest {

    @Test
    public void testBadUUIDExceptionClass() {
        String message = "Invalid UUID";
        BadUUIDException exception = new BadUUIDException(message);
        BadUUIDException exception1 = new BadUUIDException();

        assertEquals(message, exception.getMessage());

        String newMessage = "New message";
        exception.setMessage(newMessage);

        assertNotNull(exception1);
        assertEquals(newMessage, exception.getMessage());
    }
}
