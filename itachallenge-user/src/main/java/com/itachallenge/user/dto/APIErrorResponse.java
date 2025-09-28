package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class APIErrorResponse {

    @JsonProperty("error")
    String error;

    @JsonProperty("message")
    String message;

    @JsonProperty("timestamp")
    Instant timestamp;

}

