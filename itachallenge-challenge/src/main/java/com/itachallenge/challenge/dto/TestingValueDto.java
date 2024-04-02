package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TestingValueDto {

    @JsonProperty(value = "in_param", index = 0)
    private List<?> inParam;

    @JsonProperty(value = "out_param", index = 1)
    private List<?> outParam;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestingValueDto that = (TestingValueDto) o;
        return Objects.equals(inParam, that.inParam) &&
                Objects.equals(outParam, that.outParam);
    }
    @Override
    public int hashCode() {
        return Objects.hash(inParam, outParam);
    }
}