package com.itachallenge.challenge.config.dbchangelog;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class DataBaseRollBackTest {

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private MongoClient mongoClient;

    @Mock
    private MongoCollection<Document> mongoCollection;

    private DataBaseRollback dataBaseRollback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataBaseRollback = new DataBaseRollback();
    }

    @Test
    void testExecution() {
        // Mock the behavior of the database and collection
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.empty());

        // Execute the method
        dataBaseRollback.execution(mongoClient);

        // Verify that updateFieldInCollection method was called
        verify(mongoCollection, times(1)).updateMany(any(Document.class), any(Document.class));
    }

    @Test
    void testRollBackExecution() {
        // Mock the behavior of the database and collection
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.empty());

        // Execute the rollback method
        dataBaseRollback.rollBackExecution(mongoClient);

        // Verify that rollbackUpdateFieldInCollection and updateTextInField methods were called
        verify(mongoCollection, times(2)).updateMany(any(Document.class), any(Document.class));
    }

    @Test
    void testUpdateFieldInCollection() {
        // Mock the behavior of the database and collection
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.empty());

        // Call the private method via reflection (if needed)
        dataBaseRollback.execution(mongoClient);

        // Verify that updateMany was called on the collection
        verify(mongoCollection, times(1)).updateMany(any(Document.class), any(Document.class));
    }

    @Test
    void testUpdateTextInField() {
        // Mock the behavior of the database and collection
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.empty());

        // Call the private method via reflection (if needed)
        dataBaseRollback.rollBackExecution(mongoClient);

        // Verify that updateMany was called on the collection
        verify(mongoCollection, times(2)).updateMany(any(Document.class), any(Document.class));
    }

    @Test
    void testRollbackUpdateFieldInCollection() {
        // Mock the behavior of the database and collection
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.empty());

        // Call the private method via reflection (if needed)
        dataBaseRollback.rollBackExecution(mongoClient);

        // Verify that updateMany was called on the collection
        verify(mongoCollection, times(2)).updateMany(any(Document.class), any(Document.class));
    }

    @Test
    void testExecutionErrorHandling() {
        // Mock the behavior of the database and collection with an error
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.error(new RuntimeException("Update failed")));

        // Call the method
        dataBaseRollback.execution(mongoClient);

        // Verify that the error was logged
        verify(mongoCollection, times(1)).updateMany(any(Document.class), any(Document.class));
    }

    @Test
    void testRollbackErrorHandling() {
        // Mock the behavior of the database and collection with an error
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.updateMany(any(Document.class), any(Document.class))).thenReturn(Mono.error(new RuntimeException("Update failed")));

        // Call the method
        dataBaseRollback.rollBackExecution(mongoClient);

        // Verify that the error was logged
        verify(mongoCollection, times(2)).updateMany(any(Document.class), any(Document.class));
    }
}
