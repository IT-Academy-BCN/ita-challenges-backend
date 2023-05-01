package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FiltersDto {

    private List<FilterInfoDto> filtersInfo;

    private FiltersDto() {
        //for tests when deserialization
    }

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
