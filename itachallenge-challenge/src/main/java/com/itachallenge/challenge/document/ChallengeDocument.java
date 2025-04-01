package com.itachallenge.challenge.document;

import com.itachallenge.challenge.enums.Topic;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection="challenges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field(name = "challenge_title")
    private String title;

    @Field(name = "level")
    private String level;   //valor seteado fom properties

    @Field(name = "creation_date")
    private LocalDateTime creationDate;

    @Field(name = "detail")
    private DetailDocument detail;

    @Field(name = "languages")
    private Set<LanguageDocument> languages;

    @Field(name = "solutions")
    private List<UUID> solutions;

    @Field(name = "topic")
    private Topic topic;

    @Field(name = "times_favorite")
    private Integer timesFavorite;


    @Field(name = "tags")
    private Set<TagDocument> tags;

    public void setTags(TagDocument tag) {
        if (tags == null) {
            tags = new HashSet<>();
        }

        if (!tags.add(tag)) {
            tags.remove(tag);
        }
    }


    public void increaseTimesFavorite () {
            timesFavorite = timesFavorite == null ? 1 : timesFavorite + 1;
        }

        public void decreaseTimesFavorite () {
            timesFavorite = Integer.max(timesFavorite == null ? 0 : timesFavorite - 1, 0);

        }


}
