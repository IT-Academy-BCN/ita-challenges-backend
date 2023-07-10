package com.itachallenge.user.repository;

import com.itachallenge.user.hash.UserHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class UserRedisRepository {

    @Value("${redis.user.key}")
    private String KEY;
    private final ReactiveHashOperations<String,String,UserHash> hashOperations;
    private final ReactiveRedisOperations<String,UserHash> redisOperations;


    @Autowired
    public UserRedisRepository(ReactiveRedisOperations<String, UserHash> reactiveRedisOperations){
        this.hashOperations = reactiveRedisOperations.opsForHash();
        this.redisOperations = reactiveRedisOperations;
    }

    public UserHash save(UserHash user) {
        hashOperations.put(KEY, UUID.randomUUID().toString(), user);
        return user;
    }

    public void deleteByUuid(UUID uuid){
        hashOperations.remove(KEY, uuid.toString());
    }



    public void deleteAll(){
        redisOperations.delete(KEY);
    }

    public Flux<UserHash> findAll(){
        return hashOperations.values(KEY);
    }
}
