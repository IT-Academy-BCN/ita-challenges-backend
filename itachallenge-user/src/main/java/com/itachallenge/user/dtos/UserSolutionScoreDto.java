package com.itachallenge.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSolutionScoreDto {


    private String userId;

    private String challengeId;

    private String languageId;

    private String solutionText;

    private int score;


}
