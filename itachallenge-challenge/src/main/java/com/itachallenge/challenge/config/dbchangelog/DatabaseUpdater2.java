package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static com.mongodb.client.model.Updates.rename;

@ChangeUnit(id = "Provocate RollbackExecution", order = "5", author = "Dani")
public class DatabaseUpdater2 {

    private Logger logger = LoggerFactory.getLogger(DatabaseUpdater2.class);
    private ReactiveMongoTemplate reactiveMongoTemplate;
    private static final String COLLECTION_NAME = "mongockDemo";
    private static final String LANGUAGE_NAME = "language_name";


    public DatabaseUpdater2(ReactiveMongoTemplate reactiveMongoTemplate, Logger logger) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.logger = logger;
    }

    @Execution
    public void execution(MongoClient client) {
        try {
            updateFieldInCollection(client);
            addFieldToAllDocuments(reactiveMongoTemplate);
            // Intentional error to provoke rollback
            throw new IntentionalException("Intentional error occurred during execution");
        } catch (RuntimeException e) {
            throw new IntentionalException("Error occurred during execution");
        }
    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        rollbackUpdateFieldInCollection(client);
        removeFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("RollbackExecution completed successfully");
    }

    public void updateFieldInCollection(MongoClient client) {
        MongoCollection<Document> mongockTest = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(mongockTest.updateOne(new Document(), rename(LANGUAGE_NAME, "language_name_updated")))
                .doOnSuccess(updateResult -> logger.info("Field 'language_name' renamed to 'language_name_updated'"))
                .block();
    }

    public void rollbackUpdateFieldInCollection(MongoClient client) {
        MongoCollection<Document> mongockTest = client.getDatabase("challenges").getCollection(COLLECTION_NAME);
        Mono.from(mongockTest.updateOne(new Document(), rename("language_name_updated", "language_name_Rollbacked")))
                .doOnSuccess(updateResult -> logger.info("Field 'language_name_updated' renamed back to 'language_name'"))
                .block();
    }

    public void addFieldToAllDocuments(ReactiveMongoTemplate reactiveMongoTemplate) {
        Query query = new Query(where(LANGUAGE_NAME).ne(null));
        Update update = new Update().set("new_field", "new_value");
        reactiveMongoTemplate.updateMulti(query, update, COLLECTION_NAME).block();
    }

    public void removeFieldToAllDocuments(ReactiveMongoTemplate reactiveMongoTemplate) {
        Query query = new Query(where(LANGUAGE_NAME).ne(null));
        Update update = new Update().unset("new_field");
        reactiveMongoTemplate.updateMulti(query, update, COLLECTION_NAME).block();
    }
}
