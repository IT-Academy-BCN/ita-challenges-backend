package com.itachallenge.user.hash;

import com.itachallenge.user.document.Role;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@RedisHash
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserHash implements Serializable {
    private UUID uuid;
    private String name;
    private String surname;
    private String dni;
    private String nickname;
    private String email;
    private String password;
    private List<Role> roles;
}
