package com.itachallenge.challenge.config.dbchangelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeUnit(id = "DatabaseUpdaterDemo", order = "2", author = "Ernesto Arcos")
public class DatabaseUpdater {
    private final Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public DatabaseUpdater(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Execution
    public void execution() {
        addFieldToAllDocuments(reactiveMongoTemplate);
        removeFieldToAllDocuments(reactiveMongoTemplate);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("UpdaterExecution");

    }

    @RollbackExecution
    public void rollBackExecution() {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("UpdaterRollbackExecution");
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
