package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

class DatabaseUpdaterTest {
    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Mock
    MongoClient mongoClient;
    @InjectMocks
    private DatabaseUpdater databaseUpdater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executionTest() {
        ReactiveMongoTemplate reactiveMongoTemplateMock = Mockito.mock(ReactiveMongoTemplate.class);
        MongoCollection<Document> mongoCollection = Mockito.mock(MongoCollection.class);
        MongoDatabase mongoDatabase = Mockito.mock(MongoDatabase.class);
        Bson update = new Document("$rename", new Document("language_name", "language_name_updated"));
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

            when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
            when(mongoDatabase.getCollection("mongockTest")).thenReturn(mongoCollection);
            when(mongoCollection.updateOne((Bson) any(), (Bson) any())).thenReturn(updateResultPublisher);

        when(reactiveMongoTemplateMock.updateMulti(any(), any(), eq("mongockTest"))).thenReturn(Mono.empty());

        DatabaseUpdater databaseUpdater = new DatabaseUpdater(reactiveMongoTemplateMock);

        databaseUpdater.execution(mongoClient);

        verify(mongoCollection).updateOne((Bson) any(), (Bson) any());
        verify(reactiveMongoTemplateMock, times(2)).updateMulti(any(), any(), eq("mongockTest"));
    }

    @Test
    void rollBackExecutionTest() {
        ReactiveMongoTemplate reactiveMongoTemplateMock = Mockito.mock(ReactiveMongoTemplate.class);
        MongoCollection<Document> mongoCollection = Mockito.mock(MongoCollection.class);
        MongoDatabase mongoDatabase = Mockito.mock(MongoDatabase.class);
        Bson update = new Document("$rename", new Document("language_name_updated", "language_name"));
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("mongockTest")).thenReturn(mongoCollection);
        when(mongoCollection.updateOne((Bson) any(), (Bson) any())).thenReturn(updateResultPublisher);

            when(reactiveMongoTemplateMock.updateMulti(any(), any(), eq("mongockTest"))).thenReturn(Mono.empty());

    }

    @Test
    void updateFieldInCollectionTest() {
        MongoCollection<Document> mongoCollection = Mockito.mock(MongoCollection.class);
        MongoDatabase mongoDatabase = Mockito.mock(MongoDatabase.class);
        Bson update = new Document("$rename", new Document("language_name", "language_name_updated"));
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("mongockTest")).thenReturn(mongoCollection);
        when(mongoCollection.updateOne((Bson) any(), (Bson) any())).thenReturn(updateResultPublisher);

        databaseUpdater.updateFieldInCollection(mongoClient);

        verify(mongoCollection).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void rollbackUpdateFieldInCollectionTest() {
        MongoCollection<Document> mongoCollection = Mockito.mock(MongoCollection.class);
        MongoDatabase mongoDatabase = Mockito.mock(MongoDatabase.class);
        Bson update = new Document("$rename", new Document("language_name_updated", "language_name"));
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("mongockTest")).thenReturn(mongoCollection);
        when(mongoCollection.updateOne((Bson) any(), (Bson) any())).thenReturn(updateResultPublisher);

        databaseUpdater.rollbackUpdateFieldInCollection(mongoClient);

        verify(mongoCollection).updateOne((Bson) any(), (Bson) any());

    }

    @Test
    void addFieldToAllDocumentsTest() {
        Query query = new Query(where("_id").ne(null));
        Update update = new Update().set("newField", "newValue");

        when(reactiveMongoTemplate.updateMulti(query, update, "mongockTest"))
                .thenReturn(Mono.just(UpdateResult.acknowledged(1, 1L, null)));

        databaseUpdater.addFieldToAllDocuments(reactiveMongoTemplate);

        verify(reactiveMongoTemplate, times(1)).updateMulti(query, update, "mongockTest");
    }

    @Test
    void removeFieldToAllDocumentsTest() {
        Update update = new Update().unset("newField");
        Query query = new Query(where("_id").ne(null));

        when(reactiveMongoTemplate.updateMulti(query, update, "mongockTest"))
                .thenReturn(Mono.just(UpdateResult.acknowledged(1, 1L, null)));

        databaseUpdater.removeFieldToAllDocuments(reactiveMongoTemplate);

        verify(reactiveMongoTemplate, times(1)).updateMulti(query, update, "mongockTest");
    }
}