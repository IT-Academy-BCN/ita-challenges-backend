package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoTemplate<ChallengeDocument,UUID>{

/*    @Autowired
    MongoTemplate mongoTemplate;
    public Employee save(Employee emp) {
        return  mongoTemplate.save(emp);
    }*/

//private ReactiveMongoTemplate template;

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<ChallengeDocument> findByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID uuid);

    Mono<ChallengeDocument> findByLevel(String level);

    Mono<ChallengeDocument> findByTitle(String title);

    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);

    Mono<ChallengeDocument> save(ChallengeDocument challenge);


    @Query("{ 'firstname': ?0, 'lastname': ?1}")
    Flux<ChallengeDocument> findByFirstnameAndLastname(String firstname, String lastname);

//    Flux<YourDocument> yourDocumentFlux = reactiveMongoTemplate.find(new Query().skip((page - 1) * size).limit(size), YourDocument.class);
//
//    Flux<SuDocumento> suDocumentoFlux = reactiveMongoTemplate.find(nueva Consulta(), SuDocumento.clase)
//            .skip((página - 1) * tamaño)
//            .tomar(tamaño);

/*
    @Query("{'name': ?0}")
        //(value="{'name': ?0, fields= {'lastname':0, 'age': 0}")
    List<Employee> findByName(String name);

    @Query("{'age':{'$gt':'?0', 'lt':'?1'} }")
    List<Employee> findByAgeBetween(int minAge, int maxAge);

    @Query("{'name': ?0, 'department': ?1}")
    List<Employee> findByNameAndDepartment(String name, String department);


    List<Employee> findByNameOrderByDepartment(String name);*/


    /*PAGINATION:
    @Override
    public List<Person> getAllPersonPaginated(int pageNumber, int pageSize) {
        Query query = new Query();
        query.skip(pageNumber * pageSize);
        query.limit(pageSize);
        return mongoTemplate.find(query, Person.class);
    }*/


/*    // Find list of employees with salary between value1 and value2
    @Query("{ $and: [ {salary : { $gt :  ?0 }}, { salary : { $lt :  ?1 } } ]}")
    List<Employee> findEmployeesBetween(Double salary1, Double salary2);*/

/*
    Mono<NoteList> findAllNotes(String userId);

    Mono<NoteList> findAllNotesAndPaginate(String userId, int page, int limit);
*/


/*    @Override
    public Mono<NoteList> findAllNotesAndPaginate(String userId, int page, int limit) {
        // approach 1. data layer pagination
        // return repository.findByUserId(userId, PageRequest.of(page, limit)).map(list->new NoteList(list, 0));

        // approach 2. service layer pagination
        return repository.findByUserId(userId).collectList().map(list -> {
            int total = list.size();
            int start = (page - 1) * limit;
            List<Note> notes = list.stream().skip(start).limit(limit).collect(Collectors.toList());
            NoteList result = new NoteList(notes, total);
            return result;
        });*/


}
