package com.itachallenge.user.dto;

import com.itachallenge.user.dtos.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ErrorResponseDtoTest {
    final String TEST_MESSAGE = "Test message";

    @Mock
    private ErrorResponseDto mockErrorResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getErrorResponse() {
        ErrorResponseDto errorResponse = new ErrorResponseDto(TEST_MESSAGE);

        assertEquals(TEST_MESSAGE, errorResponse.getMessage());
    }

    @Test
    void setMessage() {
        ErrorResponseDto errorResponse = new ErrorResponseDto(TEST_MESSAGE);

        assertEquals(TEST_MESSAGE, errorResponse.getMessage());
    }

    @Test
    void getMockErrorResponse() {
        when(mockErrorResponse.getMessage()).thenReturn(TEST_MESSAGE);

        assertEquals(TEST_MESSAGE, mockErrorResponse.getMessage());
    }

}