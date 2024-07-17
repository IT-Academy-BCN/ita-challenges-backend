//package com.itachallenge.challenge.config.dbchangelog;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.itachallenge.challenge.document.ChallengeDocument;
//import io.mongock.api.annotations.ChangeUnit;
//import io.mongock.api.annotations.Execution;
//import io.mongock.api.annotations.RollbackExecution;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//@ChangeUnit(id = "databaseInitializer", order = "3", author = "Dani")
//public class DatabaseInitializerPrueba {
//
//
//    @Execution
//    public void loadJsonData(ReactiveMongoTemplate reactiveMongoTemplate) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(UUID.class, new UUIDDeserializer());
//        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
//        mapper.registerModule(module);
//
//        ClassPathResource resource = new ClassPathResource("mongodb-test-data/challenges.json");
//        List<ChallengeDocument> challenges = mapper.readValue(
//                resource.getInputStream(),
//                mapper.getTypeFactory().constructCollectionType(List.class, ChallengeDocument.class));
//
//        Flux.fromIterable(challenges)
//                .flatMap(reactiveMongoTemplate::save)
//                .blockLast();  // Ensures the operation completes before proceeding
//    }
//
//    @RollbackExecution
//    public void rollback(ReactiveMongoTemplate reactiveMongoTemplate) {
//        reactiveMongoTemplate.dropCollection(ChallengeDocument.class).block();
//    }
//}
