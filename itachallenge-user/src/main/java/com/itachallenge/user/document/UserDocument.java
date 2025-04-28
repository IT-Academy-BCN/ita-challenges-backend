package com.itachallenge.user.document;

import com.itachallenge.user.document.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.*;
import java.util.stream.Collectors;

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

    @Field("favorite_challenges")
    private Set<UUID> favoriteChallenges;

    @Field("bookmark_challenges")
    private Set<UUID> bookmarkChallenges;

    @Field("solved_challenges")
    private Set<UUID> solvedChallenges;

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
        if (favoriteChallenges != null && !favoriteChallenges.isEmpty()) {
            joiner.add("favoriteChallenges='");
            joiner.add(favoriteChallenges.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        }
        if (bookmarkChallenges != null && !bookmarkChallenges.isEmpty()) {
            joiner.add("bookmarkChallenges='");
            joiner.add(bookmarkChallenges.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        }
        if (solvedChallenges != null && !solvedChallenges.isEmpty()) {
            joiner.add("solvedChallenges='");
            joiner.add(solvedChallenges.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        }

        return joiner.toString();
    }

}
