package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResourceDto {

    @JsonProperty(value = "id_resource", index = 0)
    private UUID resourceId;

    @JsonProperty(value = "title", index = 1)
    private String title;

    @JsonProperty(value = "description", index = 2)
    private String description;

    @JsonProperty(value = "url", index = 3)
    private String url;

    @JsonProperty(value = "topic", index = 4)
    private String topic;

    @JsonProperty(value = "content_type", index = 5)
    private String contentType;

    @JsonProperty(value = "challenge_ids", index = 6)
    private List<UUID> challengeIds;
}