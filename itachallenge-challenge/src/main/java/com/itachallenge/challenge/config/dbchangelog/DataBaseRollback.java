package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import static com.mongodb.client.model.Updates.rename;

/*
 * This class is a change log that updates the database by adding a new field to all documents in a collection,
 * then updates the field name in all documents in the collection.
 * The class uses the reactive MongoDB driver to interact with the database.
 * The class is annotated with @ChangeUnit, which specifies the id, order, and author of the change log.
 * The class has an execution method that adds a document to the collection, adds a field to all documents in the collection,
 * and updates the field name in all documents in the collection.
 * The class has a rollbackExecution method that rolls back the changes made in the execution method.
 * If you want do a new Order, you can do a new class with the same structure and change the order in the annotation.
 *
 * @Author: Dani Diaz
 */

@Component
@ChangeUnit(id = "Intentional Rollback order", order = "5", author = "Dani Diaz")
public class DataBaseRollback {

    private Logger logger = LoggerFactory.getLogger(DataBaseRollback.class);
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private static final String COLLECTION_NAME = "mongockDemo";
    private static final String FIELD_NAME_UPDATED = "Language Rollbacked";
    private static final String FIELD_NAME = "language_name";


    public DataBaseRollback(ReactiveMongoTemplate reactiveMongoTemplate, Logger logger) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.logger = logger;
    }

    @Execution
    public void execution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution started");

        try {
            updateFieldInCollection(client);
            logger.info("Field updated in collection");


            throw new RuntimeException("Intentional exception to trigger rollback");
        } catch (RuntimeException e) {
            logger.info("Exception occurred: {}", e.getMessage(), e);
            throw e;
        }
    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution started");
        rollbackUpdateFieldInCollection(client);
        logger.info("Field updated in collection rolled back");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution completed successfully");

    }

    private void updateFieldInCollection(MongoClient client) {
        MongoCollection<Document> collection = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME_UPDATED, new Document("$exists", true)),
                        rename(FIELD_NAME_UPDATED, FIELD_NAME)))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed to '{}'", FIELD_NAME_UPDATED, FIELD_NAME_UPDATED))
                .block();
    }

    private void rollbackUpdateFieldInCollection(MongoClient client) {

        MongoCollection<Document> collection = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME, new Document("$exists", true)),
                        rename(FIELD_NAME, FIELD_NAME_UPDATED)))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed back to '{}'", FIELD_NAME, FIELD_NAME_UPDATED))
                .block();
    }
}
