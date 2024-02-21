package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestingValueDocument {

    @Field(name = "in_param")
    private List<?> inParam;

    @Field(name = "out_param")
    private List<?> outParam;

}