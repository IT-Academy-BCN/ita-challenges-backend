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

@ChangeUnit(id="DatabaseUpdaterDemo", order = "2", author = "Ernesto Arcos")
public class DatabaseUpdater {
    private final Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);

    @Execution
    public void execution(MongoClient client) {
        MongoCollection<Document> mongockTest = client.getDatabase("challenges").getCollection("mongockTest");

        Mono.from(mongockTest.updateOne(new Document(), rename("language_name", "name")))
                .doOnSuccess(updateResult -> logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~\nUpdaterExecution"))
                .subscribe();
    }

    @RollbackExecution
    public void rollBackExecution(MongoClient client) {
        MongoCollection<Document> mongockTest = client.getDatabase("challenges").getCollection("mongockTest");

        Mono.from(mongockTest.updateOne(new Document(), rename("name", "language_name")))
                .doOnSuccess(updateResult -> logger.info("~~~~~~~~~~~~~~~~~~~~~~~~\nUpdaterRollbackExecution"))
                .subscribe();
    }
}
