package com.itachallenge.challenge.document;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document
public class Challenge {
    @MongoId
    private UUID challenge_Id;

}
