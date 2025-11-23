package com.itachallenge.submission.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSubmissionGlobalExceptionHandlerTest {

        private  UserSubmissionGlobalExceptionHandler userSubmissionGlobalExceptionHandler ;

        @BeforeEach
        void setUp() {
            userSubmissionGlobalExceptionHandler = new UserSubmissionGlobalExceptionHandler();
        }

        @Test
        void testHandleAny() {
            Exception exception = new Exception("Unexpected Error");
            ResponseEntity<String> response = userSubmissionGlobalExceptionHandler.handleAny(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Unexpected error happened.", response.getBody());
        }

        @Test
        void testHandleNotFoundException() {
            NotFoundException exception = new NotFoundException("Resource not found.");
            ResponseEntity<String> response = userSubmissionGlobalExceptionHandler.handleNotFound(exception);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Resource not found.", response.getBody());
        }

        @Test
        void testHandleBadRequestException() {
            BadRequestException exception = new BadRequestException("Bad Request");
            ResponseEntity<String> response = userSubmissionGlobalExceptionHandler.handleBadRequestException(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Bad Request", response.getBody());
        }

        @Test
        void testHandleBadUUIDException() {
            BadUUIDException exception = new BadUUIDException("Bad UUID");
            ResponseEntity<String> response = userSubmissionGlobalExceptionHandler.handleBadUUID(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("The provided IDs are not valid.", response.getBody()); // Mensaje fijo del handler
        }

        @Test
        void testRuntimeExceptionFallsBackToGenericHandler() {
            RuntimeException exception = new RuntimeException("Some runtime error");
            ResponseEntity<String> response = userSubmissionGlobalExceptionHandler.handleAny(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Unexpected error happened.", response.getBody());
        }
}
