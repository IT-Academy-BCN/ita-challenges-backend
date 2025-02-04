package com.itachallenge.user.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UserDocument{");

        if (uuid != null) {
            sb.append("uuid=").append(uuid);
        }
        if (username != null) {
            if (uuid != null) sb.append(", ");  // Add a comma only if UUID is present
            sb.append("username='").append(username).append("'");
        }

        sb.append("}");
        return sb.toString();
    }


}
