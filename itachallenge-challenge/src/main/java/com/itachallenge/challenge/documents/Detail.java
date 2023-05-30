package com.itachallenge.challenge.documents;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Detail {

    private String description;

    private List<Example> examples;

    private String note;
}
