package com.itachallenge.user.repository;

import com.itachallenge.user.hash.UserHash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRedisRepository {
    private final HashOperations<String,String, UserHash> hashOperations;
    private final RedisTemplate<String,UserHash> redisTemplate;

    public UserRedisRepository(RedisTemplate<String,UserHash> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }
}
