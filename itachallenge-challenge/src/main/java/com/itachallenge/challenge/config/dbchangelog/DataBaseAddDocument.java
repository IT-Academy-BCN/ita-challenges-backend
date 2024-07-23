package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/*
 * This class is a change log that updates the database by adding a new document to a collection.
 * The class uses the reactive MongoDB driver to interact with the database.
 * The class is annotated with @ChangeUnit, which specifies the id, order, and author of the change log.
 * The class has an execution method that adds a document to the collection.
 * The class has a rollbackExecution method that rolls back the changes made in the execution method.
 * If you want to do a new Order, you can do a new class with the same structure and change the order in the annotation.
 *
 * @Author: Dani Diaz
 */

@Component
@ChangeUnit(id = "Add Documents", order = "3", author = "Dani Diaz")
public class DataBaseAddDocument {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseAddDocument.class);
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private static final String COLLECTION_NAME = "mongockDemo";
    private static final String FIELD_NAME = "Language";


    public DataBaseAddDocument(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


    @Execution
    public void execution(MongoClient client) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdater execution started");
        addDocumentToCollection(reactiveMongoTemplate);
        logger.info("Document added to collection");

    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution started");
        removeDocumentFromCollection(reactiveMongoTemplate);
        logger.info("Document removed from collection");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nRollback execution completed successfully");

    }


    private void addDocumentToCollection(ReactiveMongoTemplate mongoTemplate) {
        LanguageDocument languageDocument = new LanguageDocument(UUID.randomUUID(), "Java_Demo");
        mongoTemplate.save(languageDocument, COLLECTION_NAME)
                .doOnSuccess(success -> logger.info("Document added to collection"))
                .block();
    }

    private void removeDocumentFromCollection(ReactiveMongoTemplate mongoTemplate) {

        Query query = Query.query(where(FIELD_NAME).is("Java_Demo"));
        mongoTemplate.remove(query, COLLECTION_NAME)
                .doOnSuccess(result -> logger.info("Document removed from collection"))
                .doOnError(error -> logger.error("Error during document removal: {}", error.getMessage()))
                .subscribe();
    }
}
