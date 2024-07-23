package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.annotations.*;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@ChangeUnit(id = "DatabaseInitalizerDemo", order = "1", author = "Ernesto Arcos / Pedro López")
public class DatabaseInitializer {

    Query query = new Query(where("_id").ne(null));
    private final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String COLLECTION_NAME = "mongockDemo";

    // Method to create a new collection before the execution of the change unit
    @BeforeExecution
    public void createCollection(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.createCollection(COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("mongockDemo collection created");
    }

    // Method to rollback the changes before the execution of the change unit, in case of any failure
    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(COLLECTION_NAME).drop().subscribe(subscriber);
        subscriber.await();

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("mongockDemo collection droped");
    }

    // Method to execute the changes in the database
    @Execution
    public void execution(ReactiveMongoTemplate reactiveMongoTemplate) {
        LanguageDocument languageDocument = new LanguageDocument(UUID.randomUUID(), "JAVA");
        reactiveMongoTemplate.save(languageDocument, COLLECTION_NAME)
                .doOnSuccess(success -> logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nexecution"))
                .subscribe();
    }

    // Method to rollback the changes in case of any failure during the execution
    @RollbackExecution
    public void rollback(ReactiveMongoTemplate reactiveMongoTemplate) {
        reactiveMongoTemplate.remove(query, COLLECTION_NAME)
                .doOnSuccess(success -> logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nrollback"))
                .then();
    }
}
