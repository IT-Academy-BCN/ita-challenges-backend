package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeListDto {

    @JsonProperty(value = "results", index = 0)
    @Builder.Default
    private List<ChallengeDto> results = new ArrayList<>();

    @JsonProperty(value = "total", index = 1)
    @Builder.Default
    private Integer total = 0;
}

