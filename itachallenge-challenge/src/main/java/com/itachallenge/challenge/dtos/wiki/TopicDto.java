package com.itachallenge.challenge.dtos.wiki;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TopicDto {

    private String id;

    private String name;

    private String slug;

    private String categoryId;

    private String createdAt;

    private String updatedAt;
}
