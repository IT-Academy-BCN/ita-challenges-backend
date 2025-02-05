package com.itachallenge.user.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Document(collection="solutions")
public class UserSolutionDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field("user_id")
    private UUID userId;


}
