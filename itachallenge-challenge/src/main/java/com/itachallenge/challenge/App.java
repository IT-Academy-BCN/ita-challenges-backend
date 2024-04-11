package com.itachallenge.challenge;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.EnableMongock;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMongock
public class App {

    //Todo to avoid
    public final static String MONGODB_CONNECTION_STRING = "mongodb://admin_challenge:BYBcMJEEWw5egRUo@localhost:27017/admin?uuidRepresentation=STANDARD";
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    //Todo to check alternatives
    @Bean
    public ConnectionDriver mongockConnection(MongoClient mongoClient) {
        return MongoReactiveDriver.withDefaultLock(mongoClient, "challenges");
    }

    //Todo to check alternatives
    @Bean
    MongoClient mongoClient() {
        CodecRegistry codecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGODB_CONNECTION_STRING))
                .codecRegistry(codecRegistry)
                .build());
    }
}
