package com.itachallenge.challenge.dto.wiki;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class WikiResourceDto {

    private String id;
    private String title;
    private String slug;
    private String description;
    private String url;
    private String resourceType;
    private String createdAt;
    private String updatedAt;
    private WikiUserResourceDto user;
    private List<WikiTopicDto> topics;
}
