package com.itachallenge.user.document;

import com.itachallenge.user.document.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.StringJoiner;
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
    @Indexed(unique = true)
    private String username;

    @Field("role")
    private Role role;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "UserDocument{", "}");

        if (uuid != null) {
            joiner.add("uuid=" + uuid);
        }
        if (username != null) {
            joiner.add("username='" + username + "'");
        }
        if (role != null) {
            joiner.add("role='" + role + "'");
        }

        return joiner.toString();
    }

}
