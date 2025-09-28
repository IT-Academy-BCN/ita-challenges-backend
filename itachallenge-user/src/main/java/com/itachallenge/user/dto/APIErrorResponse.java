package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor

@Data
public class APIErrorResponse {

    @JsonProperty("error")
    String error;

    @JsonProperty("message")
    String message;

    @JsonProperty("timestamp")
    Instant timestamp;

}

