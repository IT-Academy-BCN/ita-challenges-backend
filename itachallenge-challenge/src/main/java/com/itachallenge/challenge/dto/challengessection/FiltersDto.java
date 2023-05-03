package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FiltersDto {

    private List<FilterInfoDto> filtersInfo;

    public FiltersDto() {
        //for tests when deserialization
    }

    public int getCount(){
        return filtersInfo.size();
    }
}
