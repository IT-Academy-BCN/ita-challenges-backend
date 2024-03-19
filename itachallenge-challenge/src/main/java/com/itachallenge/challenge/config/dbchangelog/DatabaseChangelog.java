package com.itachallenge.challenge.config.dbchangelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@ChangeLog(order = "001")
public class DatabaseChangelog {

    //see https://github.com/mongock/mongock-examples/tree/master/mongodb/springboot3-reactive-jdk17/src/main/java/io/mongock/examples/mongodb/springboot/reactive/migration

    @ChangeSet(order = "002", id = "addNewField", author = "developer")
    public Mono<Void> initialSetup(ReactiveMongoTemplate reactiveMongoTemplate) {
        //non-reactive TODO change it
        //db.getCollection("yourCollectionName").updateMany(new org.bson.Document(), Updates.set("newFieldName", "defaultValue"));
    return Mono.empty();
    }

    //@ChangeSet(order = "002", id = "addNewField", author = "developer")
    public void addNewField(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("yourCollectionName");
        Bson updateOperation = Updates.set("newFieldName", "defaultValue");
        collection.updateMany(new Document(), updateOperation);
    }

    //WARNING: Mongock do not provides an automatic mechanism to rollback changes.
    //It's necessary manually invoke it to do it
    public void undo(MongoDatabase db) {
        db.getCollection("yourCollectionName").updateMany(new Document(), Updates.unset("newFieldName"));
    }
}