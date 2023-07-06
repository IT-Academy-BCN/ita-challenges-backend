package com.itachallenge.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class GenericResultDto<T> {

    private int offset;
    private int limit;
    private int count;

    private T[] results;

    public GenericResultDto() {}

    public void setInfo(int offset, int limit, int count, T[] results) {
        this.offset = offset;
        this.limit = limit;
        this.count = count;
        this.results = results;
    }
}
