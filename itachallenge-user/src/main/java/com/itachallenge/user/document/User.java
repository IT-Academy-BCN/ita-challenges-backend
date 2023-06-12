package com.itachallenge.user.document;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @MongoId
    @Field(name = "uuid")
    private UUID uuid;
    @Field(name = "name")
    private String name;

    @Field(name = "surname")
    private String surname;

    @Field(name = "nickname")
    private String nickname;

    @Email
    @Field(name = "email")
    private String email;

    @Field(name = "password")
    private String password;

    @Field(name = "role")
    private List<Role> roles;

}
