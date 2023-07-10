package com.itachallenge.user.hash;

import com.itachallenge.user.document.Role;
import org.springframework.data.redis.core.RedisHash;


import java.util.List;
import java.util.UUID;

@RedisHash
public class UserHash {
    private UUID uuid;
    private String name;
    private String surname;
    private String nickname;
    private String email;
    private String password;
    private List<Role> roles;
}
