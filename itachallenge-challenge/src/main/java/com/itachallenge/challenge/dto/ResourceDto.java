package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;
import java.util.Objects;

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

    @JsonProperty(value = "resourceId", index = 0)
    @NotEmpty(message = "cannot be empty")
    private UUID resourceId;

    @JsonProperty(value = "title", index = 1)
    @NotEmpty(message = "cannot be empty")
    private String title;

    @JsonProperty(value = "description", index = 2)
    @NotEmpty(message = "cannot be empty")
    private String description;

    @JsonProperty(value = "url", index = 3)
    @NotEmpty(message = "cannot be empty")
    private String url;

    @JsonProperty(value = "topic", index = 4)
    @NotNull(message = "cannot be empty")
    private Topic topic;

    @JsonProperty(value = "contentType", index = 5)
    @NotEmpty(message = "cannot be empty")
    private ResourceContentType contentType;

    @JsonProperty(value = "challengeIds", index = 6)
    @NotNull(message = "cannot be empty")
    private List<UUID> challengeIds;

    @JsonProperty(value = "associationType", index = 7)
    @NotNull(message = "cannot be empty")
    private AssociationType associationType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceDto that = (ResourceDto) o;
        return Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(url, that.url) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(challengeIds, that.challengeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, title, description, url, topic, contentType, challengeIds);
    }

}