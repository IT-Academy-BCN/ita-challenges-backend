package com.itachallenge.errorcore.builder;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.dto.FieldErrorDto;
import com.itachallenge.errorcore.exception.BaseApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class ErrorResponseBuilder {

    private final MessageSource messageSource;

    /** Build general error response.*/
    public APIErrorResponse buildError(
            Exception e,
            HttpServletRequest request
    ) {
        ExceptionMapping mapping = mapException(e);
        return APIErrorResponse.builder()
                .status(mapping.status.value())
                .error(mapping.status.getReasonPhrase())
                .message(resolveMessage(mapping.messageKey))
                .path(request != null ? request.getRequestURI() : null)
                .build();
    }

    public APIErrorResponse buildCustomExceptionError(
            BaseApiException e,
            HttpServletRequest request
    ) {
        ExceptionMapping mapping = mapException(e);
        return APIErrorResponse.builder()
                .status(mapping.status.value())
                .error(mapping.status.getReasonPhrase())
                .message(resolveMessage(mapping.messageKey,e.getInfo().messageArgs()))
                .path(request != null ? request.getRequestURI() : null)
                .build();
    }

    public APIErrorResponse buildArgumentNotValidErrorResponse(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String objectName = ex.getBindingResult().getObjectName();
        return buildValidationErrorResponse(
                resolveMessage(mapException(ex).messageKey,objectName),
                extractFieldErrors(ex),
                request
        );
    }

    public APIErrorResponse buildConstraintViolationErrorResponse(ConstraintViolationException ex, HttpServletRequest request) {
        return buildValidationErrorResponse(
                resolveMessage(mapException(ex).messageKey),
                extractConstraintViolations(ex),
                request
        );
    }

    public APIErrorResponse buildTypeMismatchErrorResponse(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildValidationErrorResponse(
                resolveMessage(
                        mapException(ex).messageKey,
                        ex.getName(),                                   // {0} parameter name
                        ex.getValue(),                                  // {1} invalid value
                        ex.getRequiredType() != null
                                ? ex.getRequiredType().getSimpleName()
                                : "unknown"                                 // {2} expected type
                ),
                singleFieldErrorList(ex),
                request
        );
    }

    public APIErrorResponse buildStatusErrorResponse(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatusCode statusCode = ex.getStatusCode();
        String errorMessage = resolveMessage(mapException(ex).messageKey);
        // Build the APIErrorResponse using your builder (same message text)
        return APIErrorResponse.builder()
                .status(statusCode.value())
                .error(ex.getReason())
                .message(errorMessage)
                .path(request != null ? request.getRequestURI() : null)
                .build();
    }

    // --- PRIVATE HELPERS ----------------------------------------------------
    /** Converts a validation violation into a detailed field error DTO. */
    private FieldErrorDto toFieldErrorDto(ConstraintViolation<?> violation) {
        String fieldName = extractFieldName(violation);
        String detailedMessage = resolveMessage(
                "validation.constraint.detailed",
                fieldName,
                violation.getMessage()
        );
        return FieldErrorDto.builder()
                .objectName(violation.getRootBeanClass().getSimpleName())
                .field(fieldName)
                .message(detailedMessage)
                .build();
    }

    /** Extracts field-level errors from @Valid annotated DTOs. */
    private List<FieldErrorDto> extractFieldErrors(MethodArgumentNotValidException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> FieldErrorDto.builder()
                        .objectName(error.getObjectName())
                        .field(error.getField())
                        .message(messageSource.getMessage(error, locale))
                        .build())
                .toList();
    }

    /** Extracts constraint violations from @Validated annotated method parameters. */
    private List<FieldErrorDto> extractConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(this::toFieldErrorDto)
                .toList();
    }

    /** Creates a singleton list of FieldErrorDto for type mismatch or single-parameter errors. */
    private List<FieldErrorDto> singleFieldErrorList(MethodArgumentTypeMismatchException ex) {
        String field = ex.getName();
        String rejectedValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        FieldErrorDto fieldError = FieldErrorDto.builder()
                .objectName(ex.getParameter().getContainingClass().getSimpleName())
                .field(field)
                .message(resolveMessage(mapException(ex).messageKey,
                        field,
                        rejectedValue,
                        requiredType)
                )
                .build();

        return List.of(fieldError);
    }

    /** Extracts the last segment of the property path (the field name). */
    private String extractFieldName(ConstraintViolation<?> violation) {
        return violation.getPropertyPath().toString()
                .replaceAll("^.*\\.", ""); // e.g., user.email -> email
    }

    /** Build validation error response.*/
    private APIErrorResponse buildValidationErrorResponse(
            String message,
            List<FieldErrorDto> fieldErrors,
            HttpServletRequest request
    ) {
        return APIErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .errors(fieldErrors)
                .path(request != null ? request.getRequestURI() : null)
                .build();
    }

    /** Resolve message from message source. */
    public String resolveMessage(String messageKey) {
        return resolveMessage(messageKey, new Object[]{});
    }
    private String resolveMessage(String messageKey, Object arg1) {
        return resolveMessage(messageKey, new Object[]{arg1});
    }
    private String resolveMessage(String messageKey, Object arg1, Object arg2) {
        return resolveMessage(messageKey, new Object[]{arg1, arg2});
    }
    private String resolveMessage(String messageKey, Object arg1, Object arg2, Object arg3) {
        return resolveMessage(messageKey, new Object[]{arg1, arg2, arg3});
    }
    private String resolveMessage(String messageKey, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(messageKey, args, locale);
        } catch (Exception e) {
            log.debug("No message found for key '{}', using literal", messageKey);
            return messageKey; // fallback to literal if no translation found
        }
    }

    // --- Exception Mapping----------------------------------------------------

    /** Internal record representing the mapping of an exception to a message key and HTTP status. */
    private record ExceptionMapping(HttpStatus status, String messageKey) {}

    /** Determines the appropriate message key and HTTP status for a given exception. */
    private ExceptionMapping mapException(Exception e) {
        // --- Validation & argument errors ---
        if (e instanceof BaseApiException bae)
            return new ExceptionMapping(bae.getInfo().status(), bae.getInfo().messageKey());

        if (e instanceof MethodArgumentNotValidException)
            return new ExceptionMapping(HttpStatus.BAD_REQUEST, "validation.argument_not_valid");

        if (e instanceof ConstraintViolationException)
            return new ExceptionMapping(HttpStatus.BAD_REQUEST, "validation.constraint");

        if (e instanceof MethodArgumentTypeMismatchException)
            return new ExceptionMapping(HttpStatus.BAD_REQUEST, "validation.type_mismatch");

        if (e instanceof IllegalArgumentException)
            return new ExceptionMapping(HttpStatus.BAD_REQUEST, "validation.illegal_argument");

        if (e instanceof InvalidFormatException
                || e instanceof HttpMessageNotReadableException
                || e instanceof ServerWebInputException
                || e instanceof DecodingException)
            return new ExceptionMapping(HttpStatus.BAD_REQUEST, "validation.bad_request");

        // --- ResponseStatusException (explicit HTTP semantics) ---
        if (e instanceof ResponseStatusException rse)
            return new ExceptionMapping(HttpStatus.valueOf(rse.getStatusCode().value()),
                    "status.exception." + rse.getStatusCode().value());
        // --- Default / fallback case ---
        log.warn("Unhandled exception type in ErrorResponseBuilder: {}", e.getClass().getName());
        return new ExceptionMapping(HttpStatus.INTERNAL_SERVER_ERROR, "internal.server_error");
    }



}
