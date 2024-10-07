package com.itachallenge.challenge.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new CustomCacheManager();
    }

    static class CustomCacheManager implements CacheManager {

        private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

        @Override
        public Cache getCache(String name) {
            return cacheMap.computeIfAbsent(name, CustomCache::new);
        }

        @Override
        public Collection<String> getCacheNames() {
            return cacheMap.keySet();
        }
    }

    static class CustomCache extends ConcurrentMapCache {

        private final ConcurrentMap<Object, Long> expirationMap = new ConcurrentHashMap<>();
        private final long expirationTime = TimeUnit.MINUTES.toMillis(10); // Set cache duration here

        public CustomCache(String name) {
            super(name);
        }

        @Override
        public void put(Object key, Object value) {
            super.put(key, value);
            expirationMap.put(key, System.currentTimeMillis() + expirationTime);
        }

        @Override
        public ValueWrapper get(Object key) {
            if (isExpired(key)) {
                evict(key);
                return null;
            }
            return super.get(key);
        }

        @Override
        public void evict(Object key) {
            super.evict(key);
            expirationMap.remove(key);
        }

        private boolean isExpired(Object key) {
            Long expirationTime = expirationMap.get(key);
            return expirationTime == null || System.currentTimeMillis() > expirationTime;
        }
    }
}