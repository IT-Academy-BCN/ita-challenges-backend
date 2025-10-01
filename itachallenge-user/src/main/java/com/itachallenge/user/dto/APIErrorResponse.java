package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@Setter
public class APIErrorResponse {

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("status")
    private int status;

    @JsonProperty("path")
    private String path;

    public APIErrorResponse(String error, String message, HttpStatus status, String path){
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now();
        this.status = status.value();
        this.path = path;

    }

}

