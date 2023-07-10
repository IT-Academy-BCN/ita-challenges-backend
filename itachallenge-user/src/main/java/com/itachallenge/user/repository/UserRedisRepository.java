package com.itachallenge.user.repository;

import com.itachallenge.user.hash.UserHash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public class UserRedisRepository {

    @Value("redis.user.key")
    private String HASH_KEY;
    private final HashOperations<String,String,UserHash> hashOperations;

    public UserRedisRepository(RedisTemplate<String,UserHash> redisTemplate){
        this.hashOperations = redisTemplate.opsForHash();
    }

    public UserHash save(UserHash object) {
        hashOperations.put(HASH_KEY, UUID.randomUUID().toString(), object);
        return object;
    }

    public Map<String,UserHash> findAll(){
        return hashOperations.entries(HASH_KEY);
    }
}
