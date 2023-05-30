package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    @MongoId
    @Field(name="id_challenge")
    private UUID uuid;

    private String level;   //valor seteado fom properties

    private String title;

    private Set<Language> languages;

    private LocalDate creation_date;

    private Detail detail;

    private List<Solution> solutions;

    private Set<UUID> relateds;

    private Set<UUID> resources;

}
