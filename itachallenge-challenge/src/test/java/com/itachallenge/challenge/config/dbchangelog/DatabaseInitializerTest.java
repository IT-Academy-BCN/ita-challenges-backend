package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class DatabaseInitializerTest {

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private DatabaseInitializer databaseInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        databaseInitializer = new DatabaseInitializer();
    }

    @Test
    void testCreateCollection() {
        when(mongoDatabase.createCollection(any())).thenReturn(Mono.empty());

        databaseInitializer.createCollection(mongoDatabase);

        verify(mongoDatabase, times(1)).createCollection(any());
    }

    @Test
    void testRollbackBeforeExecution() {
        MongoCollection<Document> mongoCollection = Mockito.mock(MongoCollection.class);

        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);

        when(mongoCollection.drop()).thenReturn(Mono.empty());

        databaseInitializer.rollbackBeforeExecution(mongoDatabase);

        verify(mongoCollection, times(1)).drop();
    }

    @Test
    void testExecution() {
        when(reactiveMongoTemplate.save(any(LanguageDocument.class), any())).thenReturn(Mono.just(new LanguageDocument()));

        databaseInitializer.execution(reactiveMongoTemplate);

        verify(reactiveMongoTemplate, times(1)).save(any(LanguageDocument.class), any());
    }

    @Test
    void testRollback() {
        when(reactiveMongoTemplate.remove(any(Query.class), anyString())).thenReturn(Mono.empty());

        databaseInitializer.rollback(reactiveMongoTemplate);

        verify(reactiveMongoTemplate, times(1)).remove(any(Query.class), anyString());
    }
}
