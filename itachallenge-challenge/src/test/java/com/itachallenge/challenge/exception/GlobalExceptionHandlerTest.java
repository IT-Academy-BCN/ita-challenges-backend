package com.itachallenge.challenge.exception;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.repository.*;
import com.itachallenge.challenge.service.*;
import com.itachallenge.jwtcore.service.IJwtService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.MessageSource;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GlobalExceptionHandlerTest.class)
class GlobalExceptionHandlerTest {
    //VARIABLES
    String REQUEST = "Invalid request";
    private final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private final HttpStatus OK_REQUEST = HttpStatus.OK;
    private final HttpStatus NOT_FOUND_REQUEST = HttpStatus.NOT_FOUND;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @MockBean
    private ResponseStatusException responseStatusException;
    @MockBean
    private MessageDto errorMessage;
    @MockBean
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @MockBean
    private DiscoveryClient discoveryClient;
    @MockBean
    private IChallengeService challengeService;
    @MockBean
    private IUserService userService;
    @MockBean
    private IFavoriteService favoriteService;
    @MockBean
    private ITagService tagService;
    @MockBean
    private IResourceService resourceService;
    @MockBean
    private ILanguageService languageService;
    @MockBean
    private WebClient.Builder webClientBuilder;
    @MockBean
    private ChallengeRepository challengeRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private SolutionRepository solutionRepository;
    @MockBean
    private ResourceRepository resourceRepository;
    @MockBean
    private LanguageRepository languageRepository;
    @MockBean
    private PropertiesConfig config;
    @MockBean
    private IJwtService jwtService;
    @MockBean
    private MappingMongoConverter mappingMongoConverter;
    @MockBean
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleResponseStatusException() {

        // Arrange
        String expectedErrorMessage = "Validation failed";
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        ResponseStatusException ex = new ResponseStatusException(expectedStatus, expectedErrorMessage);

        GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);

        // Act
        ResponseEntity<MessageDto> responseEntity = handler.handleResponseStatusException(ex);

        // Assert
        assertEquals(expectedStatus, responseEntity.getStatusCode());
        assertEquals(expectedErrorMessage, responseEntity.getBody().getMessage());
    }

    @Test
    void testHandleResponseStatusException_NullDetailMessageArguments() {
        // Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        ResponseStatusException ex = mock(ResponseStatusException.class);
        when(ex.getStatusCode()).thenReturn(expectedStatus);
        when(ex.getDetailMessageArguments()).thenReturn(null);

        GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);

        // Act
        ResponseEntity<MessageDto> responseEntity = handler.handleResponseStatusException(ex);

        // Assert
        assertEquals(expectedStatus, responseEntity.getStatusCode());
        assertEquals("Validation failed", Objects.requireNonNull(responseEntity.getBody()).getMessage());
    }

    @Test
    void TestHandleMethodArgumentNotValidException() {

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "message")));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);

        // Assert
        MatcherAssert.assertThat(responseEntity, notNullValue());
    }

    @Test
    void TestHandleMethodArgumentNotValidException_Return_DefaultMessage() {

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("name");
        when(fieldError.getDefaultMessage()).thenReturn("default message");
        when(fieldError.getCodes()).thenReturn(new String[]{"message"});
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);

        // Assert
        MatcherAssert.assertThat(responseEntity, notNullValue());
    }

    @Test
    void handleConstraintViolation() {
        // Arrange
        Set<ConstraintViolation<?>> constraints = new HashSet<>();

        ConstraintViolation<?> constraint1 = mock(ConstraintViolation.class);
        when(constraint1.getMessage()).thenReturn("Expected message");
        constraints.add(constraint1);

        ConstraintViolation<?> constraint2 = mock(ConstraintViolation.class);
        when(constraint2.getMessage()).thenReturn("Expected message");
        constraints.add(constraint2);

        ConstraintViolationException exception = new ConstraintViolationException("Validation failed.", constraints);

        // Act
        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleConstraintViolation(exception);

        // Assert
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        String responseBody = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        Assertions.assertTrue(responseBody.contains("Expected message"));
    }


    @Test
    void testHandleChallengeNotFoundException() {
        // Arrange
        ChallengeNotFoundException challengeNotFoundException = new ChallengeNotFoundException("Challenge not found");

        // Act
        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleChallengeNotFoundException(challengeNotFoundException);

        // Assert
        assertEquals(NOT_FOUND_REQUEST, responseEntity.getStatusCode());
        String responseBody = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        Assertions.assertTrue(responseBody.contains("Challenge not found"));
    }

    @Test
    void testHandleResourceNotFoundException() {
        // Testgi
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Resource not found");

        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleResourceNotFoundException(resourceNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        String responseBody = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        Assertions.assertTrue(responseBody.contains("Resource not found"));
    }

    @Test
    void testHandleNotFoundException() {
        // Arrange
        NotFoundException notFoundException = new NotFoundException("Whatever not found");

        // Act
        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleNotFoundException(notFoundException);

        // Assert
        assertEquals(OK_REQUEST, responseEntity.getStatusCode());
        String responseBody = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        Assertions.assertTrue(responseBody.contains("Whatever not found"));
    }

    @Test
    void test_HandleBadUUIDException() {
        // Arrange
        BadUUIDException badUUIDException = new BadUUIDException("Invalid Id format");

        // Act
        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleBadUUIDException(badUUIDException);

        // Assert
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        String responseBody = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        Assertions.assertTrue(responseBody.contains("Invalid Id format"));
    }

    @Test
    void testHandleLanguageNotFoundException() {

        LanguageNotFoundException exception = new LanguageNotFoundException("Language not found");

        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleLanguageNotFoundException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String responseBody = responseEntity.getBody().getMessage();
        assertTrue(responseBody.contains("Language not found"));
    }

    @Test
    void testHandleCustomInternalServerErrorException() {

        InternalServerErrorException exception = new InternalServerErrorException("Error message");

        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleCustomInternalServerErrorException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        String responseBody = responseEntity.getBody().getMessage();
        assertTrue(responseBody.contains("Error message"));
    }

    @Test
    void testHandleTagNotFoundException() {

        TagNotFoundException exception = new TagNotFoundException("Tag not found");

        ResponseEntity<MessageDto> responseEntity = globalExceptionHandler.handleTagNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        String responseBody = responseEntity.getBody().getMessage();
        assertTrue(responseBody.contains("Tag not found"));
    }

    @Test
    void testHandleInvalidFormat_TagsField() {
        InvalidFormatException ex = InvalidFormatException.from(
                null,
                "cannot deserialize value of type java.util.UUID from String \"invalid-uuid\"",
                "invalid-uuid",
                UUID.class
        );

        ex.prependPath(new Reference(null, "tags"));

        ResponseEntity<MessageDto> resp = globalExceptionHandler.handleInvalidFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals(
                "invalid format UUID tag: invalid-uuid",
                Objects.requireNonNull(resp.getBody()).getMessage()
        );
    }

    @Test
    void testHandleInvalidFormat_OtherFieldFallback() {
        InvalidFormatException ex = InvalidFormatException.from(
                null,
                "cannot deserialize value of type java.util.UUID from String \"invalid-uuid\"",
                "invalid-uuid",
                UUID.class
        );
        ex.prependPath(new Reference(null, "otherField"));

        ResponseEntity<MessageDto> resp = globalExceptionHandler.handleInvalidFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals(
                ex.getOriginalMessage(),
                Objects.requireNonNull(resp.getBody()).getMessage()
        );
    }
}
