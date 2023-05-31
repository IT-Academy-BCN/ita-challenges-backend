package com.itachallenge.user.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.itachallenge.user.repository")
@PropertySource("classpath:application-test.yml")
public class DBTestConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public MongoClient mongoClient() {
        String host = env.getProperty("spring.data.mongodb.host");
        int port = Integer.parseInt(env.getProperty("spring.data.mongodb.port"));
        return MongoClients.create(String.format("mongodb://%s:%d", host, port));
    }

    @Bean
    public ReactiveMongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        String databaseName = env.getProperty("spring.data.mongodb.database");
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, databaseName);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory databaseFactory) {
        return new ReactiveMongoTemplate(databaseFactory);
    }
}
