package com.itachallenge.user.service;

import com.itachallenge.user.document.Role;
import com.itachallenge.user.document.User;
import com.itachallenge.user.hash.UserHash;
import com.itachallenge.user.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserHashService {
    private final UserRedisRepository userRedisRepository;

    public Mono<UserHash> hazrandom() {
        UserHash user = UserHash.builder()
                .uuid(UUID.randomUUID())
                .name("John")
                .surname("Doe")
                .nickname("johndoe")
                .email("johndoe@example.com")
                .password("password123")
                .roles(List.of(Role.ADMIN, Role.USER))
                .build();

        return userRedisRepository.save(user);
    }

    public Mono<UserHash> getone(UUID uuid){
        return userRedisRepository.findByUuid(uuid);
    }

    public Flux<UserHash> getall(){
        return userRedisRepository.findAll();
    }

    public Mono<Void> borraUno(UUID uuid) {
        return userRedisRepository.deleteByUuid(uuid);
    }
}
