package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class APIErrorResponse {

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("status")
    private int status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("path")
    private String path;

    public APIErrorResponse(HttpStatus status, String error, String message, String path){
        this.timestamp = Instant.now();
        this.status = status.value();
        this.error = error;
        this.message = message;
        this.path = path;
    }


}
