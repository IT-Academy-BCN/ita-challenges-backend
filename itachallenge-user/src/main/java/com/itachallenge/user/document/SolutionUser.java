package com.itachallenge.user.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.UUID;
/**
 * @author Luis
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "solutionsUsers")
public class SolutionUser {
    @Id
    @Field("_id")
    private UUID uuid_solutionUser;
    @Field(name = "uuid_challenge")
    private UUID uuid_challenge;
    @Field(name = "uuid_language")
    private UUID uuid_language;
    @Field(name = "uuid_user")
    private UUID uuid_user;
    @Field(name = "solution_text")
    private String solution_Text;
}