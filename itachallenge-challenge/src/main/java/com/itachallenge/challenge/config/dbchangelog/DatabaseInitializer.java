package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.LanguageDocument;
import io.mongock.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeUnit(id = "DatabaseInitalizerDemo", order = "1", author = "Ernesto Arcos")
public class DatabaseInitializer {
    Query query = new Query(where("_id").ne(null));
    private final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String COLLECTION_NAME = "mongockTest";

    @BeforeExecution
    public void createCollection(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(COLLECTION_NAME);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("mongockTest collection created");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection(COLLECTION_NAME);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("mongockTest collection droped");
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        LanguageDocument languageDocument = new LanguageDocument((UUID.randomUUID()), "LanguageDemo");
        mongoTemplate.save(languageDocument, COLLECTION_NAME);

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("execution");
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.remove(query, COLLECTION_NAME);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("rollback");
    }

}
