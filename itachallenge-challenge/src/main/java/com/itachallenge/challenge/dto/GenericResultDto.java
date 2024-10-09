package com.itachallenge.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class GenericResultDto<T> {

    private int count;
    private int offset;
    private int limit;

    private T[] results;

    public GenericResultDto() {}

    public GenericResultDto(int count,int offset, int limit, T[] results) {
        this.count = count;
        this.offset = offset;
        this.limit = limit;
        this.results = results;
    }


    public void setInfo(int count,int offset, int limit, T[] results) {
        this.count = count;
        this.offset = offset;
        this.limit = limit;
        this.results = results;
    }
}
