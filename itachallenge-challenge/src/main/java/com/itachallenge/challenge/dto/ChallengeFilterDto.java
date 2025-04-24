package com.itachallenge.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ChallengeFilterDto {

    private String idLanguage;
    private String level;
    private List<UUID> tags;
    private Integer offset = 0;
    private Integer limit = -1;
}
