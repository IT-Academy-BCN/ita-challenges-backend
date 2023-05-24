package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="resources")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Resource {
    @Id
    private UUID resourceId;

    @Field(name="resource")
    private String resource;
}