package com.itachallenge.challenge.dto.zmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.dto.TestingValueDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class TestingValuesResponseDto {
    @JsonProperty(value="id_challenge")
    private UUID challengeId;

    @JsonProperty(value = "testing_values")
    private List<TestingValueDto> testingValues;

}
