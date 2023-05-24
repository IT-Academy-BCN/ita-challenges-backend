package com.itachallenge.challenge.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class PageDto {

    private int offset;
    private int limit;
    private List<Object> results;

    public int getCount(){
        return results.size();
    }
}
