package com.itachallenge.challenge.config.dbchangelog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.document.ChallengeDocument;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.annotations.*;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.query.Query;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@ChangeUnit(id = "DataBaseInitializerPrueba2", order = "4", author = "Dani")
public class DatabaseInitializerPrueba2 {

    Query query = new Query(where("_id").ne(null));
    private final Logger logger = LoggerFactory.getLogger(DatabaseInitializerPrueba2.class);

    private static final String CHALLENGE_COLLECTION_NAME = "challenges";
    private static final String LANGUAGE_COLLECTION_NAME = "languages";
    private static final String SOLUTION_COLLECTION_NAME = "solutions";

    @BeforeExecution
    public void createCollections(MongoDatabase mongoDataBase)
    {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDataBase.createCollection(CHALLENGE_COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();

        mongoDataBase.createCollection(LANGUAGE_COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();

        mongoDataBase.createCollection(SOLUTION_COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Collections created");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(CHALLENGE_COLLECTION_NAME).drop().subscribe(subscriber);
        subscriber.await();

        mongoDatabase.getCollection(LANGUAGE_COLLECTION_NAME).drop().subscribe(subscriber);
        subscriber.await();

        mongoDatabase.getCollection(SOLUTION_COLLECTION_NAME).drop().subscribe(subscriber);
        subscriber.await();

        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Collections dropped");
    }

    @Execution
    public void execution(ReactiveMongoTemplate reactiveMongoTemplate) {
        try {
            InputStream challenges = getClass().getClassLoader().getResourceAsStream("mongodb-test-data/challenges.json");
            InputStream languages = getClass().getClassLoader().getResourceAsStream("mongodb-test-data/languages.json");
            InputStream solutions = getClass().getClassLoader().getResourceAsStream("mongodb-test-data/solutions.json");

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> challengeDocuments = mapper.readValue(challenges, new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> languageDocuments = mapper.readValue(languages, new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> solutionDocuments = mapper.readValue(solutions, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> challengeDocument : challengeDocuments) {
                Document mongoDocument = new Document(challengeDocument);
                processSpecialFields(mongoDocument);
                reactiveMongoTemplate.insert(mongoDocument, CHALLENGE_COLLECTION_NAME).subscribe();
            }

            for (Map<String, Object> languageDocument : languageDocuments) {
                Document mongoDocument = new Document(languageDocument);
                processSpecialFields(mongoDocument);
                reactiveMongoTemplate.insert(mongoDocument, LANGUAGE_COLLECTION_NAME).subscribe();
            }

            for (Map<String, Object> solutionDocument : solutionDocuments) {
                Document mongoDocument = new Document(solutionDocument);
                processSpecialFields(mongoDocument);
                reactiveMongoTemplate.insert(mongoDocument, SOLUTION_COLLECTION_NAME).subscribe();
            }

            logger.info("Challenges loaded");

        } catch (Exception e) {
            logger.error("Error loading data", e);
        }
    }

    private void processSpecialFields(Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                if (valueMap.containsKey("$uuid")) {
                    entry.setValue(java.util.UUID.fromString((String) valueMap.get("$uuid")));
                } else if (valueMap.containsKey("$date")) {
                    entry.setValue(new java.util.Date((long) valueMap.get("$date")));
                } else {
                    processSpecialFields(new Document(valueMap));
                }
            } else if (value instanceof List) {
                for (Object item : (List) value) {
                    if (item instanceof Map) {
                        processSpecialFields(new Document((Map<String, Object>) item));
                    }
                }
            }
        }
    }

    @RollbackExecution
    public void rollbackExecution(ReactiveMongoTemplate reactiveMongoTemplate) {
        reactiveMongoTemplate.remove(query, ChallengeDocument.class, CHALLENGE_COLLECTION_NAME).subscribe();
        logger.info("Challenges removed");
    }
}
