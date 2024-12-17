package com.itachallenge.user.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor // Required in order to use the Builder

@Builder // This helps in the creation of the instance

public class ChallengesListsDto {
    private List<UserChallengeDto> completed;
    private List<UserChallengeDto> saved;
}