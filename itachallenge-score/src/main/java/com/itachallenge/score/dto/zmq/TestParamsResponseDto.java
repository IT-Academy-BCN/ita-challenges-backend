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
            for (TestingValueDto value : testingValues) {
                List<?> inParams = value.getInParam();
                List<?> outParams = value.getOutParam();

                if (inParams != null && !inParams.isEmpty() && outParams != null && !outParams.isEmpty()) {
                    String formattedEntry = String.format("{%s}={%s}", inParams.get(0), outParams.get(0));
                    params.add(formattedEntry);
                }
            }
        }
        return params;
    }
}
