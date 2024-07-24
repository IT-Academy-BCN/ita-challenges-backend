package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Updates.rename;

/*
 * This class is a change log that updates the database by adding a new field to all documents in a collection,
 * then updates the field name in all documents in the collection.
 * The class uses the reactive MongoDB driver to interact with the database.
 * The class is annotated with @ChangeUnit, which specifies the id, order, and author of the change log.
 * The class do an intentional rollback of the changes made in the execution method to demonstrate the rollback feature.
 * If you want to do a new Order, you can do a new class with the same structure and change the order in the annotation.
 *
 * @Author: Dani Diaz
 */

@Component
@ChangeUnit(id = "Intentional Rollback order", order = "5", author = "Dani Diaz")
public class DataBaseRollback {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseRollback.class);

    private static final String DATABASE_NAME = "challenges";
    private static final String COLLECTION_NAME = "mongockDemo";
    private static final String FIELD_NAME_UPDATED = "Language Rollbacked";
    private static final String FIELD_NAME = "language_name";


    @Execution
    public void execution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution started");

            updateFieldInCollection(client);
            logger.info("Field updated in collection");
    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution started");

        rollbackUpdateFieldInCollection(client);
        updateTextInField(client);
        logger.info("Field updated in collection rolled back");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution completed successfully");

    }

    private void updateFieldInCollection(MongoClient client) {

        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        Document updateQuery = new Document("invalidOperator", new Document("$invalid", "someValue"));

        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME_UPDATED, new Document("$exists", true)),
                        updateQuery))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed to '{}'", FIELD_NAME, FIELD_NAME_UPDATED))
                .doOnError(error -> logger.error("Update failed: {}", error.getMessage()))
                .block();
    }


    private void updateTextInField(MongoClient client) {
        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

        Document filter = new Document(FIELD_NAME_UPDATED, "LanguageDemo");
        Document update = new Document("$set", new Document(FIELD_NAME_UPDATED, "LanguageUpdated"));

        Mono.from(collection.updateMany(filter, update))
                .doOnSuccess(updateResult -> logger.info("Field '{}' updated from 'LanguageDemo' to 'LanguageUpdateD'", FIELD_NAME_UPDATED))
                .block();
    }



    private void rollbackUpdateFieldInCollection(MongoClient client) {

        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        Mono.from(collection.updateMany(
                        new Document(FIELD_NAME, new Document("$exists", true)),
                        rename(FIELD_NAME, FIELD_NAME_UPDATED)))
                .doOnSuccess(updateResult -> logger.info("Field '{}' renamed back to '{}'", FIELD_NAME, FIELD_NAME_UPDATED))
                .block();
    }
}
