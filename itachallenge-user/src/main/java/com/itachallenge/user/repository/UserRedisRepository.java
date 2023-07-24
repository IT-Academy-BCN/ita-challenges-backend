package com.itachallenge.user.repository;

import com.itachallenge.user.hash.UserHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserRedisRepository {

    @Value("${redis.user.key}")
    private String KEY;
    private final ReactiveHashOperations<String,UUID,UserHash> hashOperations;
    private final ReactiveRedisOperations<String,UserHash> redisOperations;


    @Autowired
    public UserRedisRepository(ReactiveRedisOperations<String, UserHash> reactiveRedisOperations){
        this.hashOperations = reactiveRedisOperations.opsForHash();
        this.redisOperations = reactiveRedisOperations;
    }

    //Save and update, if the hash key exists, it updates the existing hash
    public Mono<UserHash> save(UserHash user) {
        return hashOperations.put(KEY, user.getUuid(), user).thenReturn(user);

    }

    //returns the number of items deleted
    public Mono<Void> deleteByUuid(UUID uuid){
        return hashOperations.remove(KEY, uuid).flatMap((u) -> Mono.empty());
    }

    public Mono<UserHash> findByUuid(UUID uuid){
        return hashOperations.get(KEY,uuid);
    }

    public Mono<Boolean> existsByUuid(UUID uuid){
        return hashOperations.hasKey(KEY,uuid);
    }


    //returns the number of items deleted
    public Mono<Long> deleteAll(){
        return redisOperations.delete(KEY);
    }

    public Flux<UserHash> findAll(){
        return hashOperations.values(KEY);
    }

}
