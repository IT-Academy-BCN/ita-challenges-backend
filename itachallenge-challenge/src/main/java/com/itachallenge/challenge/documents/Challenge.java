package com.itachallenge.challenge.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Challenge {

    @Id
    private String id;
    private String name;



}
