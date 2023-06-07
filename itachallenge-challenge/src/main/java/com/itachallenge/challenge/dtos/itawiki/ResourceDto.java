package com.itachallenge.challenge.dtos.itawiki;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ResourceDto {

    private String id;

    private String title;

    private String slug;

    private String description;

    private String url;

    private String resourceType;

    private String createdAt;

    private String updatedAt;

    private UserResourceDto user;

    private List<TopicDto> topics;
}
