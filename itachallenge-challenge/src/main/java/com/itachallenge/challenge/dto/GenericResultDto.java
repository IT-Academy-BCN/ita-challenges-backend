package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class GenericResultDto<T> {

    @JsonProperty(index = 0)
    private int count;
    @JsonProperty(index = 1)
    private int offset;
    @JsonProperty(index = 2)
    private int limit;
    @JsonProperty(index = 3)
    private T[] results;

    public GenericResultDto() {}

    public void setInfo(int offset, int limit, int count, T[] results) {
        this.offset = offset;
        this.limit = limit;
        this.count = count;
        this.results = results;
    }

}
