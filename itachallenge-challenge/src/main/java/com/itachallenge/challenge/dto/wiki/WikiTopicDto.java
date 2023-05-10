package com.itachallenge.challenge.dto.wiki;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WikiTopicDto {

    private String id;
    private String name;
    private String slug;
    private String categoryId;
    private String createdAt;
    private String updatedAt;
}
