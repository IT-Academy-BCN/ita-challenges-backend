package com.itachallenge.user.exception;

import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {
    @ExceptionHandler(GithubUnavailableException.class)
    public ResponseEntity<APIErrorResponse> handleGithubUnavailable(GithubUnavailableException ex, HttpServletRequest request) {
        HttpStatus status;
        String securedMessage;
        log.error("GithubUnavailableException occurred: {}", ex.getMessage(), ex);

        if (ex.getCause() instanceof java.net.SocketTimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            securedMessage = "The external GitHub service timed out.";
        } else if (ex.getCause() instanceof java.net.ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            securedMessage = "The external GitHub service is currently unavailable.";
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            securedMessage = "An external service error occurred.";
        }
        return ResponseEntity.status(status).body(
                APIErrorResponse.builder()
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .message(securedMessage)
                                .path(request.getRequestURI())
                                .build());
    }
}