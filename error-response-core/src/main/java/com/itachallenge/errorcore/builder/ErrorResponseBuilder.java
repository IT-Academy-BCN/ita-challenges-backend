package com.itachallenge.errorcore.builder;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.dto.FieldErrorDto;
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
        HttpStatus status = mapToStatus(e);
        return APIErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(resolveMessage(mapToMessageKey(e)))
                .path(request != null ? request.getRequestURI() : null)
                .build();
    }

    public APIErrorResponse buildNotFoundError(HttpServletRequest request, String customMessage){
        return buildError(HttpStatus.NOT_FOUND,
                resolveMessage("error.notFound",customMessage),
                request);
    }

    public APIErrorResponse buildArgumentNotValidErrorResponse(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String objectName = ex.getBindingResult().getObjectName();
        return buildValidationErrorResponse(
                resolveMessage(mapToMessageKey(ex),objectName),
                extractFieldErrors(ex),
                request
        );
    }

    public APIErrorResponse buildConstraintViolationErrorResponse(ConstraintViolationException ex, HttpServletRequest request) {
        return buildValidationErrorResponse(
                resolveMessage(mapToMessageKey(ex)),
                extractConstraintViolations(ex),
                request
        );
    }

    public APIErrorResponse buildTypeMismatchErrorResponse(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildValidationErrorResponse(
                resolveMessage(
                        mapToMessageKey(ex),
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
        String errorMessage = resolveMessage(mapToMessageKey(ex));
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
                .message(resolveMessage(mapToMessageKey(ex),
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

    // --- PRIVATE HELPERS ----------------------------------------------------

    /**
     * Maps known exception types to message keys for i18n lookup.
     * Used to ensure consistent error localization across handlers.
     */
    private String mapToMessageKey(Exception e) {

        // Validation & argument-related
        if (e instanceof MethodArgumentNotValidException) return "validation.argument_not_valid";
        if (e instanceof ConstraintViolationException)   return "validation.constraint";
        if (e instanceof MethodArgumentTypeMismatchException) return "validation.type_mismatch";
        if (e instanceof IllegalArgumentException)       return "validation.illegal_argument";
        if (e instanceof InvalidFormatException)         return "validation.bad_request";
        if (e instanceof HttpMessageNotReadableException
                || e instanceof ServerWebInputException
                || e instanceof DecodingException)       return "validation.bad_request";

        // HTTP / status-driven
        if (e instanceof ResponseStatusException ex) {
            return "status.exception." + ex.getStatusCode().value();
        }

        // Fallback / uncategorized
        return "internal.server_error";
    }

    private HttpStatus mapToStatus(Exception e) {
        if (e instanceof MethodArgumentNotValidException
                || e instanceof ConstraintViolationException
                || e instanceof MethodArgumentTypeMismatchException
                || e instanceof IllegalArgumentException
                || e instanceof InvalidFormatException
                || e instanceof HttpMessageNotReadableException
                || e instanceof ServerWebInputException
                || e instanceof DecodingException) {
            return HttpStatus.BAD_REQUEST;
        }

        if (e instanceof ResponseStatusException rse) {
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


}
