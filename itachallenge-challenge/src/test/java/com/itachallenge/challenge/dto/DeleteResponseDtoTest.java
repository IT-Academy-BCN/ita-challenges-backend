package com.itachallenge.challenge.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = { // <-- New Annotation
        "token.signing.key=c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0",
        "token.expiration.minutes=600"
})
class DeleteResponseDtoTest {
    @Test
    void testMessageAndID() {
        // Arrange
        String id = "valid_id";
        String message = "Expected message";

        // Act
        DeleteResponseDto deleteResponseDto = new DeleteResponseDto(id,message);

        // Assert
        Assertions.assertEquals(message, deleteResponseDto.getMessage());
        Assertions.assertEquals(id, deleteResponseDto.getId());
    }


}
