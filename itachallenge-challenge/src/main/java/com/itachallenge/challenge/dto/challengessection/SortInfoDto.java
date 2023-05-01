package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;

import java.util.List;

@Getter
public class SortInfoDto {

    private final List<String> options;

    private SortInfoDto(List<String> options) {
        this.options = options;
    }

    public static SortInfoDto withAllOptions(){
        return new SortInfoDto(List.of("Popularidad","Fecha")); //TODO: get from enum
    }

    public int getCount(){
        return options.size();
    }
}
