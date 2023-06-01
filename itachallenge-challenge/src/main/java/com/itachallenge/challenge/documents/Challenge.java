package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document(collection="challenges")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Challenge {

    @MongoId
    @Field(name = "id_challenge")
    private UUID uuid;

}
