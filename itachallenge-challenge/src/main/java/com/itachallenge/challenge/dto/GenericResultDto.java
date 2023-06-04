package com.itachallenge.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Getter
@Setter
public class GenericResultDto<T> {

    private int offset;
    private int limit;
    private int count;

    //private Flux<T> results;
    private List<T> results;
    public GenericResultDto() {}

    public void setInfo(int offset, int limit, int count, List<T>  results) {
        this.offset = offset;
        this.limit = limit;
        this.count = count;
        this.results = results;
    }
}
