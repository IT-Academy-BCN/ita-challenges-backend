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

import static com.mongodb.client.model.Updates.rename;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeUnit(id="DatabaseUpdaterDemo", order = "2", author = "Ernesto Arcos / Pedro López")
public class DatabaseUpdater {
    private final Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public DatabaseUpdater(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Execution
    public void execution(MongoClient client) {
        updateFieldInCollection(client);
        addFieldToAllDocuments(reactiveMongoTemplate);
        removeFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("UpdaterExecution");

    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        rollbackUpdateFieldInCollection(client);
        removeFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("UpdaterRollbackExecution");
    }

    private void updateFieldInCollection(MongoClient client){
        MongoCollection<Document> mongockTest = client.getDatabase("challenges").getCollection("mongockTest");
        Mono.from(mongockTest.updateOne(new Document(), rename("language_name", "language_name_updated")))
                .doOnSuccess(updateResult -> logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdaterExecution"))
                .subscribe();
    }

    private void rollbackUpdateFieldInCollection(MongoClient client){
        MongoCollection<Document> mongockTest = client.getDatabase("challenges").getCollection("mongockTest");

        Mono.from(mongockTest.updateOne(new Document(), rename("language_name_updated", "language_name")))
                .doOnSuccess(updateResult -> logger.info("~~~~~~~~~~~~~~~~~~~~~~~~\nUpdaterRollbackExecution"))
                .subscribe();
    }

    public void addFieldToAllDocuments(ReactiveMongoTemplate reactiveMongoTemplate) {
        Update update = new Update().set("newField", "newValue");
        Query query = new Query(where("_id").ne(null));

        reactiveMongoTemplate.updateMulti(query, update, "mongockTest")
                .doOnSuccess(success -> {
                    logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    logger.info("addFieldToAllDocuments");
                })
                .subscribe();
    }

    public void removeFieldToAllDocuments(ReactiveMongoTemplate reactiveMongoTemplate) {
        Update update = new Update().unset("newField");
        Query query = new Query(where("_id").ne(null));

        reactiveMongoTemplate.updateMulti(query, update, "mongockTest")
                .doOnSuccess(success -> {
                    logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    logger.info("removeFieldFromAllDocuments");
                })
                .subscribe();
    }

}
