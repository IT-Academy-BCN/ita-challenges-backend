package com.itachallenge.challenge.config;


import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.runner.springboot.EnableMongock;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
@EnableMongock
public class MongockConfig {

/*    @Bean
    public MongockApplicationRunner mongockApplicationRunner(MongoClient mongoClient, ReactiveMongoTemplate reactiveMongoTemplate, ApplicationContext springContext) {
        return MongockSpring5.builder()
                .setDriver(SpringDataMongo3Driver.withDefaultLock(mongoClient, "yourDbName"))
                .addChangeLogsScanPackage("com.itachallenge.challenge.changelog")
                .setSpringContext(springContext)
                .buildApplicationRunner();
    }*/
}
