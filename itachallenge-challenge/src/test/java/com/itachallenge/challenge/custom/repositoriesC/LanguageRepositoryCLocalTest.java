package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
//@ExtendWith(SpringExtension.class)
//@SpringBootTest

//@PropertySource("classpath:persistence-test.properties")
@ActiveProfiles("test")
class LanguageRepositoryCLocalTest {

    @Autowired
    private LanguageRepositoryC languageRepo;

    @Autowired
    private Environment environment;

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
        asserLocalDBCollectionIsEmpty(languageRepo);
        LanguageDocC languagePersisted = languageRepo.save(languageDoc).block();
        assertEquals(1,languageRepo.count().block());
        assertThat(languagePersisted).isEqualTo(languagePersisted);
    }

    @Test
    @DisplayName("Try to duplicate ID test")
    void exceptionWhenDuplicatedIdTest(){
        asserLocalDBCollectionIsEmpty(languageRepo);
        languageRepo.save(languageDoc).block();
        LanguageDocC otherLanguageWithSameId = new LanguageDocC(languageId, "otherName");
        assertThrows(DuplicateKeyException.class,()-> languageRepo.insert(otherLanguageWithSameId).block());
    }

    @Test
    @DisplayName("Find by ID test")
    void findByIdTest(){
        asserLocalDBCollectionIsEmpty(languageRepo);
        assertFalse(languageRepo.existsById(languageId).block());
        languageRepo.save(languageDoc).block();
        assertTrue(languageRepo.existsById(languageId).block());
        LanguageDocC langaugeFound = languageRepo.findById(languageId).block();
        assertThat(languageDoc).usingRecursiveComparison().isEqualTo(langaugeFound);
    }

    @Test
    @DisplayName("Delete by Id test")
    void deleteByIdTest(){
        asserLocalDBCollectionIsEmpty(languageRepo);
        languageRepo.save(languageDoc).block();
        assertTrue(languageRepo.existsById(languageId).block());
        languageRepo.deleteById(languageId).block();
        assertEquals(0, languageRepo.count().block());
    }

    @Test
    @DisplayName("Delete all test")
    void deleteAllTest(){
        asserLocalDBCollectionIsEmpty(languageRepo);
        languageRepo.save(languageDoc).block();
        languageRepo.save(new LanguageDocC(languageId +1, "otherName")).block();
        assertEquals(2, languageRepo.count().block());
        languageRepo.deleteAll().block();
        assertEquals(0, languageRepo.count().block());
    }

    static void asserLocalDBCollectionIsEmpty(ReactiveMongoRepository<?,?> repository){
        Long count = repository.count().block();
        //System.out.println(count);
        assertEquals(0,count);
    }
}
