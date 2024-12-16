package com.itachallenge.score.dto.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class TestParamsResponseDto {
    @JsonProperty(value="uuid_challenge")
    private UUID uuidChallenge;

    @JsonProperty(value = "testing_values")
    private List<TestingValueDto> testingValues;

    public List<String> toParamList() {
        List<String> params = new ArrayList<>();
        params.add("UUID Challenge: " + uuidChallenge);

        if (testingValues != null) {
            for (int i = 0; i < testingValues.size(); i++) {
                TestingValueDto value = testingValues.get(i);
                params.add("Testing Value " + (i + 1) + ":");
                params.add("  In Params: " + value.getInParam());
                params.add("  Out Params: " + value.getOutParam());
            }
        }

        return params;
    }
}
