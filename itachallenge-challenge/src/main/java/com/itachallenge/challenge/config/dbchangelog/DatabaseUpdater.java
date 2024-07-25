package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@ChangeUnit(id="DatabaseUpdaterDemo", order = "2", author = "Daniel Diaz")
public class DatabaseUpdater {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private static final String DATABASE_NAME = "challenges";
    private static final String COLLECTION_NAME = "mongockDemo";
    private static final String FIELD_NAME = "language_name";
    private static final String NEW_FIELD_NAME = "Language Name Updated";
    private static final String STATE_FIELD = "State";
    private static final String ERROR_UPDATE = "Error during update: {}";
    private static final String EXIST = "$exists";
    // Constructor to initialize the ReactiveMongoTemplate
    public DatabaseUpdater(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    // Execution method that is called to perform the database update operations
    @Execution
    public void execution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution started");

        addFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("Field added to all documents");

        updateFieldInCollection(client);
        logger.info("Field updated in collection");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution completed successfully");
    }

    // Rollback method that is called to revert the database update operations in case of any failure
    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution started");
        rollbackUpdateFieldInCollection(client);
        logger.info("Field updated in collection rolled back");

        removeFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("Field removed from all documents");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution completed successfully");

    }

    // Method to update a field in a collection
    public void updateFieldInCollection(MongoClient client){
        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME, new Document(EXIST, true)),
                        new Document("$rename", new Document(FIELD_NAME, NEW_FIELD_NAME))
                )).doOnSuccess(updateResult ->
                        logger.info("Field '{}' renamed to '{}'", FIELD_NAME, NEW_FIELD_NAME))
                .doOnError(error ->
                        logger.error(ERROR_UPDATE, error.getMessage()))
                .subscribe();
    }
    // Method to roll back the update operation performed on a field in a collection
    public void rollbackUpdateFieldInCollection(MongoClient client){
        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(NEW_FIELD_NAME, new Document(EXIST, true)),
                        new Document("$rename", new Document(NEW_FIELD_NAME, FIELD_NAME))
                )).doOnSuccess(updateResult ->
                        logger.info("Field '{}' renamed back to '{}'", NEW_FIELD_NAME, FIELD_NAME))
                .doOnError(error ->
                        logger.error("Error during rollback: {}", error.getMessage()))
                .subscribe();
    }


    // Method to add a new field to all documents in a collection
    public void addFieldToAllDocuments(ReactiveMongoTemplate reactiveMongoTemplate) {
        reactiveMongoTemplate.updateMulti(
                query(where(FIELD_NAME).exists(true)), // Query to match all documents with the FIELD_NAME
                update(STATE_FIELD, "ACTIVE"),
                COLLECTION_NAME
        ).doOnSuccess(result -> {
            logger.info("Matched count: {}", result.getMatchedCount());
            logger.info("Modified count: {}", result.getModifiedCount());
        }).doOnError(error -> logger.error(ERROR_UPDATE, error.getMessage())).subscribe();
    }

    // Method to remove a field from all documents in a collection
    public void removeFieldToAllDocuments(ReactiveMongoTemplate reactiveMongoTemplate) {
        Query query = Query.query(where(FIELD_NAME).exists(true));
        reactiveMongoTemplate.updateMulti(query, new Update().unset(STATE_FIELD), COLLECTION_NAME)
                .defaultIfEmpty(UpdateResult.unacknowledged()) // Manejo del Mono vacío
                .doOnSuccess(result -> {
                    logger.info("Matched count: {}", result.getMatchedCount());
                    logger.info("Modified count: {}", result.getModifiedCount());
                })
                .doOnError(error -> logger.error(ERROR_UPDATE, error.getMessage()))
                .subscribe();
    }

}
