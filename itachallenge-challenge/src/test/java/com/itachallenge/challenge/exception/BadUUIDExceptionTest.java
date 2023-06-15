package com.itachallenge.challenge.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BadUUIDExceptionTest {

    @Test
    public void testBadUUIDExceptionClass() {
        String message = "Invalid UUID";
        BadUUIDException exception = new BadUUIDException(message);

        assertEquals(message, exception.getMessage());
    }
}
