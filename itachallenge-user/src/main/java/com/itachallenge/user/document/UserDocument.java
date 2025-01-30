package com.itachallenge.user.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Document(collection="users")
public class UserDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field("username")
    private String username;


}
