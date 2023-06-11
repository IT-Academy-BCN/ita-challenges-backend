package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TODO: informar que l'anotació NO es necesaria. Ja que el valor de la propietat es sobreescriu dinamicament
// (obtenint-lo de lo que hagi configurat el container)
//@PropertySource("classpath:persistence-test.properties") //used this properties to disable username:password in mongodb url

/*
TODO: advertir que l'anotació es redundant (default es PER_METHOD)

Not needed. See javadoc:
If @TestInstance is not explicitly declared on a test class or on a test interface
implemented by a test class, the lifecycle mode will implicitly default to PER_METHOD.

https://www.baeldung.com/junit-testinstance-annotation
 */
//@TestInstance(TestInstance.Lifecycle.PER_METHOD)

@DataMongoTest //disables autoconfiguration of components not related to mongo
@Testcontainers
class LanguageRepositoryCContainerTest {

    @Autowired
    private LanguageRepositoryC languageRepo;

    @Autowired
    private Environment environment;

    /**
     * Constructs single node MongoDB REPLICA SET for testing transactions.
     * Due it's static -> this container will be shared between all test methods
     *
     * From JavaDoc
     *  Containers declared as static fields will be shared between test methods.
     *  They will be started only once before any test method is executed and stopped after
     *  the last test method has executed
     *
     */
    //TODO: advertir que, encara que el @TestInstance té un Lifecycle.PER_METHOD (el default), el
    // container és compartit per tots els métodes (NO es tomba i es torna a aixecar per a cada @Test)
    @Container //in conjuntion with @Testcontainers
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    /*
    per a modificar de manera dinámica propietats en el Enviroment:
    enlloc d'agafar-les d'un 'properties' X (que té els valors preconfigurats) ->
     */
    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("challenges"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        //Notar que el replica set NO té seguretat
        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("challenges"));
    }

    private final Integer languageId = 1;

    private final LanguageDocC languageDoc = new LanguageDocC(languageId, "name1");


    @AfterEach
    void initLocalDB(){
        languageRepo.deleteAll().block();
    }

    @Test
    void demoInit(){
        Assertions.assertNotNull(languageRepo);
        Assertions.assertNotNull(environment);
        System.out.println(environment.getProperty("spring.data.mongodb.uri"));
    }

    @Test
    @DisplayName("Save one language document test")
    void saveOneTest(){
        asserDBCollectionInContainerIsEmpty(languageRepo);
        LanguageDocC languagePersisted = languageRepo.save(languageDoc).block();
        assertEquals(1,languageRepo.count().block());
        assertThat(languagePersisted).isEqualTo(languagePersisted);
    }

    @Test
    @DisplayName("Try to duplicate ID test")
    void exceptionWhenDuplicatedIdTest(){
        asserDBCollectionInContainerIsEmpty(languageRepo);
        languageRepo.save(languageDoc).block();
        LanguageDocC otherLanguageWithSameId = new LanguageDocC(languageId, "otherName");
        assertThrows(DuplicateKeyException.class,()-> languageRepo.insert(otherLanguageWithSameId).block());
    }

    @Test
    @DisplayName("Find by ID test")
    void findByIdTest(){
        asserDBCollectionInContainerIsEmpty(languageRepo);
        assertFalse(languageRepo.existsById(languageId).block());
        languageRepo.save(languageDoc).block();
        assertTrue(languageRepo.existsById(languageId).block());
        LanguageDocC langaugeFound = languageRepo.findById(languageId).block();
        assertThat(languageDoc).usingRecursiveComparison().isEqualTo(langaugeFound);
    }

    @Test
    @DisplayName("Delete by Id test")
    void deleteByIdTest(){
        asserDBCollectionInContainerIsEmpty(languageRepo);
        languageRepo.save(languageDoc).block();
        assertTrue(languageRepo.existsById(languageId).block());
        languageRepo.deleteById(languageId).block();
        assertEquals(0, languageRepo.count().block());
    }

    @Test
    @DisplayName("Delete all test")
    void deleteAllTest(){
        asserDBCollectionInContainerIsEmpty(languageRepo);
        languageRepo.save(languageDoc).block();
        languageRepo.save(new LanguageDocC(languageId +1, "otherName")).block();
        assertEquals(2, languageRepo.count().block());
        languageRepo.deleteAll().block();
        assertEquals(0, languageRepo.count().block());
    }

    static void asserDBCollectionInContainerIsEmpty(ReactiveMongoRepository<?,?> repository){
        Long count = repository.count().block();
        //System.out.println(count);
        assertEquals(0,count);
    }

}
