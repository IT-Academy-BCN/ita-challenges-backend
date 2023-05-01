package com.itachallenge.challenge.dto.challengessection;

import java.util.List;

public class FiltersDto {

    private List<FilterInfoDto> filtersInfo;

    private FiltersDto(List<FilterInfoDto> filtersInfo) {
        this.filtersInfo = filtersInfo;
    }

    public static FiltersDto withAllFilters(){
        return new FiltersDto(List.of(FilterInfoDto.forLanguages(), FilterInfoDto.forDifficulties(), FilterInfoDto.forProgress()));
    }

    public int getCount(){
        return filtersInfo.size();
    }
}
