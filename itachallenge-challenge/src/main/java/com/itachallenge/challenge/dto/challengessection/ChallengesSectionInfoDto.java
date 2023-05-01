package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengesSectionInfoDto {

    private FiltersDto filters;

    private SortInfoDto sortInfo;

    private ChallengesSectionInfoDto() {
        //for tests when deserialization
    }

    private ChallengesSectionInfoDto(FiltersDto filters, SortInfoDto sortInfo) {
        this.filters = filters;
        this.sortInfo = sortInfo;
    }

    public static ChallengesSectionInfoDto withAllOptions(){
        return new ChallengesSectionInfoDto(FiltersDto.withAllFilters(), SortInfoDto.withAllOptions());
    }
}
