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
import java.util.stream.Collectors;


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
        if (testingValues != null) {
            for (TestingValueDto value : testingValues) {
                String inParam = value.getInParam().stream()
                        .map(obj -> (obj instanceof String) ? "\"" + obj + "\"" : obj.toString())
                        .collect(Collectors.joining(","));

                String outParam = value.getOutParam().stream()
                        .map(obj -> (obj instanceof String) ? "\"" + obj + "\"" : obj.toString())
                        .collect(Collectors.joining(","));


                params.add(inParam + "=" + outParam);
            }
        }

        return params;
    }
}
