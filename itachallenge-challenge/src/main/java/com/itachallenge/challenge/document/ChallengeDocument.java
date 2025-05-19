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

    @Field(name="times_bookmark")
    private Integer timesBookmark;

    @Field(name="times_solved")
    private Integer timesSolved;

    @Field(name = "tags")
    private List<UUID> tags;

    public void increaseTimesFavorite () {
            timesFavorite = timesFavorite == null ? 1 : timesFavorite + 1;
        }

        public void decreaseTimesFavorite () {
            timesFavorite = Integer.max(timesFavorite == null ? 0 : timesFavorite - 1, 0);

        }

    public void increaseTimesBookmark() {
        timesBookmark = timesBookmark == null ? 1 : timesBookmark + 1;
    }

    public void decreaseTimesBookmark() {
        timesBookmark =Integer.max(timesBookmark == null ? 0 : timesBookmark - 1, 0);
    }
  
    public void increaseTimesSolved() {
        timesSolved = timesSolved == null ? 1 : timesSolved + 1;
    }


}
