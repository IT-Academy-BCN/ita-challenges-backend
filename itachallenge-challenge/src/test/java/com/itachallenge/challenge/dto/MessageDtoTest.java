package com.itachallenge.challenge.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class MessageDtoTest {
    @Test
    void testMessage() {
        // Arrange
        String message = "Expected message";

        // Act
        MessageDto errorResponseMessage = new MessageDto(message);

        // Assert
        Assertions.assertEquals(message, errorResponseMessage.getMessage());
    }

    @Test
    void testNotExpectedMessage() {
        // Arrange
        String message = "Expected message";
        String notExpectedMessage = "Not expected message.";

        // Act
        MessageDto errorResponseMessage = new MessageDto(message);

        // Assert
        Assertions.assertNotEquals(notExpectedMessage, errorResponseMessage.getMessage());
    }

}
