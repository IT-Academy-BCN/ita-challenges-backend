package com.itachallenge.user.dtos.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ScoreResponseDto {

    private UUID uuidChallenge;

    private UUID uuidLanguage;

    private String solutionText;

    private int score;

    private String errors;
}