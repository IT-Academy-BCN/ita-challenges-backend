package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;

class DatabaseUpdaterUnitTest {

    final static String COLLECTION_NAME = "mongockDemo";
    final static String FIELD_NAME = "language_name_updated";
    final static String STATE_FIELD = "State";
    final static String NEW_FIELD_NAME = "LanguageUpdated4";
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

    @DisplayName("Test execution method - in DatabaseUpdater")
    @Test
    void executionTest() {

        ReactiveMongoTemplate reactiveMongoTemplateMock = mock(ReactiveMongoTemplate.class);
        MongoCollection<Document> mongoCollection = mock(MongoCollection.class);


        UpdateResult updateResult = mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

        // Configurar mocks para MongoClient y MongoDatabase
        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(COLLECTION_NAME)).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Bson.class), any(Bson.class))).thenReturn(updateResultPublisher);
        when(reactiveMongoTemplateMock.updateMulti(any(), any(), eq(COLLECTION_NAME))).thenReturn(empty());

        DatabaseUpdater databaseUpdater = new DatabaseUpdater(reactiveMongoTemplateMock);
        databaseUpdater.execution(mongoClient);

        verify(mongoCollection).updateMany(any(Bson.class), any(Bson.class));
        verify(reactiveMongoTemplateMock, times(1)).updateMulti(any(Query.class), any(Update.class), eq(COLLECTION_NAME));
    }

    @DisplayName("Test rollBackExecution method - in DatabaseUpdater")
    @Test
    void rollBackExecutionTest() {

        ReactiveMongoTemplate reactiveMongoTemplateMock = mock(ReactiveMongoTemplate.class);
        MongoCollection<Document> mongoCollection = mock(MongoCollection.class);


        UpdateResult updateResult = mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = just(updateResult);

        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("mongockDemo")).thenReturn(mongoCollection);
        when(mongoCollection.updateMany((Bson) any(), (Bson) any())).thenReturn(updateResultPublisher);
        when(reactiveMongoTemplateMock.updateMulti(any(), any(), eq("mongockDemo"))).thenReturn(empty());

        DatabaseUpdater databaseUpdater = new DatabaseUpdater(reactiveMongoTemplateMock);
        databaseUpdater.rollBackExecution(mongoClient);

        verify(mongoCollection).updateMany((Bson) any(), (Bson) any());

    }

    @Test
    void updateFieldInCollectionTest() {

        MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
        Bson filter = new Document(FIELD_NAME, new Document("$exists", true));
        Bson update = new Document("$rename", new Document(FIELD_NAME, NEW_FIELD_NAME));
        UpdateResult updateResult = mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(COLLECTION_NAME)).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(filter, update)).thenReturn(updateResultPublisher);

        databaseUpdater.updateFieldInCollection(mongoClient);

        verify(mongoCollection).updateMany(filter, update);
    }

    @Test
    void rollbackUpdateFieldInCollectionTest() {

        MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
        Bson filter = new Document(NEW_FIELD_NAME, new Document("$exists", true));
        Bson update = new Document("$rename", new Document(NEW_FIELD_NAME, FIELD_NAME));
        UpdateResult updateResult = mock(UpdateResult.class);
        Publisher<UpdateResult> updateResultPublisher = Mono.just(updateResult);

        when(mongoClient.getDatabase("challenges")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(COLLECTION_NAME)).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(filter, update)).thenReturn(updateResultPublisher);

        databaseUpdater.rollbackUpdateFieldInCollection(mongoClient);
        verify(mongoCollection).updateMany(filter, update);
    }

    @Test
    void addFieldToAllDocumentsTest() {

        Query query = Query.query(where(FIELD_NAME).exists(true));
        Update update = new Update().set(STATE_FIELD, "ACTIVE");

        UpdateResult updateResult = UpdateResult.acknowledged(1, 1L, null);

        when(reactiveMongoTemplate.updateMulti(query, update, COLLECTION_NAME))
                .thenReturn(Mono.just(updateResult));

        databaseUpdater.addFieldToAllDocuments(reactiveMongoTemplate);
        verify(reactiveMongoTemplate, times(1)).updateMulti(query, update, COLLECTION_NAME);
    }


    @Test
    void removeFieldToAllDocumentsTest() {

        Query query = Query.query(where(FIELD_NAME).exists(true));
        Update update = new Update().unset(STATE_FIELD);

        when(reactiveMongoTemplate.updateMulti(query, update, COLLECTION_NAME))
                .thenReturn(Mono.just(UpdateResult.acknowledged(1, 1L, null)));

        databaseUpdater.removeFieldToAllDocuments(reactiveMongoTemplate);
        verify(reactiveMongoTemplate, times(1)).updateMulti(query, update, COLLECTION_NAME);
    }

}