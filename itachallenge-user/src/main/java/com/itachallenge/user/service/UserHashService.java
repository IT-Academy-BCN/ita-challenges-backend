package com.itachallenge.user.service;

import com.itachallenge.user.document.Role;
import com.itachallenge.user.hash.UserHash;
import com.itachallenge.user.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserHashService {
    private final UserRedisRepository userRedisRepository;

    public Flux<UserHash> prueba(){

        UserHash user = UserHash.builder()
                .uuid(UUID.randomUUID())
                .name("John")
                .surname("Doe")
                .nickname("johndoe")
                .email("john.doe@example.com")
                .password("password123")
                .roles(Arrays.asList(Role.ADMIN, Role.USER))
                .build();


        userRedisRepository.save(user);


        return userRedisRepository.findAll();
    }
}
