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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static com.mongodb.client.model.Updates.set;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static com.mongodb.client.model.Updates.rename;

/*
 * This class is a change log that updates the database by adding a new field to all documents in a collection,
 * then updates the field name in all documents in the collection.
 * The class uses the reactive MongoDB driver to interact with the database.
 * The class is annotated with @ChangeUnit, which specifies the id, order, and author of the change log.
 * The class has an execution method that adds a document to the collection, adds a field to all documents in the collection,
 * and updates the field name in all documents in the collection.
 * The class has a rollbackExecution method that rolls back the changes made in the execution method.
 * If you want to do a new Order, you can do a new class with the same structure and change the order in the annotation.
 *
 * @Author: Dani Diaz
 */
@Component
@ChangeUnit(id = "Update Documentss", order = "4", author = "Dani Diaz")
public class DataBaseUpdateDocument {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseUpdateDocument.class);
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private static final String COLLECTION_NAME = "mongockDemo";
    private static final String FIELD_NAME = "language_name_updated";
    private static final String NEW_FIELD_NAME = "LanguageUpdated4";
    private static final String STATE_FIELD = "State";

    public DataBaseUpdateDocument(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Execution
    public void execution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution started");

        addFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("Field added to all documents");

        updateFieldInCollection(client);
        logger.info("Field updated in collection");

        updateTextField(client);
        logger.info("Text updated in field");

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution completed successfully");
    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution started");
        rollbackUpdateFieldInCollection(client);
        logger.info("Field updated in collection rolled back");

        removeFieldFromDocuments(reactiveMongoTemplate);
        logger.info("Field removed from all documents");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution completed successfully");

    }

    private void updateFieldInCollection(MongoClient client) {
        MongoCollection<Document> collection = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME, new Document("$exists", true)),
                        rename(FIELD_NAME, NEW_FIELD_NAME)))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed to '{}'", FIELD_NAME, NEW_FIELD_NAME))
                .block();
    }

    private void rollbackUpdateFieldInCollection(MongoClient client) {

        MongoCollection<Document> collection = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(NEW_FIELD_NAME, new Document("$exists", true)),
                        rename(NEW_FIELD_NAME, FIELD_NAME)))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed back to '{}'", NEW_FIELD_NAME, FIELD_NAME))
                .block();
    }

    private void addFieldToAllDocuments(ReactiveMongoTemplate mongoTemplate) {

        mongoTemplate.updateMulti(
                Query.query(where(FIELD_NAME).exists(true)), // Query to match all documents with the FIELD_NAME
                Update.update(STATE_FIELD, "ACTIVE"),
                COLLECTION_NAME
        ).doOnSuccess(result -> {
            logger.info("Matched count: {}", result.getMatchedCount());
            logger.info("Modified count: {}", result.getModifiedCount());
        }).doOnError(error -> logger.error("Error during update: {}", error.getMessage())).block();
    }

    private void removeFieldFromDocuments(ReactiveMongoTemplate mongoTemplate) {

        Query query = Query.query(where(FIELD_NAME).exists(true));
        mongoTemplate.updateMulti(query, new Update().unset(STATE_FIELD), COLLECTION_NAME)
                .doOnSuccess(result -> {
                    logger.info("Matched count: {}", result.getMatchedCount());
                    logger.info("Modified count: {}", result.getModifiedCount());
                })
                .doOnError(error -> logger.error("Error during update: {}", error.getMessage()))
                .subscribe();
    }

    private void updateTextField(MongoClient client) {
        MongoCollection<Document> collection = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME, new Document("$exists", true)),
                        set(FIELD_NAME, "New Text")))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed text '{}'", FIELD_NAME, "New Text"))
                .block();
    }
}
