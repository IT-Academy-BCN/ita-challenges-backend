package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document(collection="challenges")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Challenge {

    @MongoId
    @Field(name = "id_challenge")
    private UUID uuid;

}
