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

    private final Integer languageId = 1000;


    @Test
    void demoInit(){
        Assertions.assertNotNull(environment);
        System.out.println(environment.getProperty("spring.data.mongodb.uri"));
    }

    @Test
    @DisplayName("Save one language document test")
    void saveOneTest(){
        Long previousCount = languageRepo.count().block();

        Integer id = getInexistentId();
        LanguageDocC languageDoc = new LanguageDocC(id, "someName");

        LanguageDocC languagePersisted = languageRepo.save(languageDoc).block();
        assertEquals(previousCount+1,languageRepo.count().block());
        assertThat(languagePersisted).isEqualTo(languageDoc);

        languageRepo.deleteById(id).block();
    }

    @Test
    @DisplayName("Try to duplicate ID test")
    void exceptionWhenDuplicatedIdTest(){
        Integer id = getInexistentId();
        LanguageDocC languageDoc = new LanguageDocC(id, "someName");
        LanguageDocC otherLanguageWithSameId = new LanguageDocC(id, "otherName");

        languageRepo.save(languageDoc).block();
        assertThrows(DuplicateKeyException.class,()-> languageRepo.insert(otherLanguageWithSameId).block());

        languageRepo.deleteById(id).block();
    }

    @Test
    @DisplayName("Find by ID test")
    void findByIdTest(){
        Integer id = getInexistentId();
        LanguageDocC languageDoc = new LanguageDocC(id, "someName");

        languageRepo.save(languageDoc).block();

        assertTrue(languageRepo.existsById(languageId).block());

        LanguageDocC langaugeFound = languageRepo.findById(languageId).block();
        assertThat(languageDoc).usingRecursiveComparison().isEqualTo(langaugeFound);

        languageRepo.deleteById(id).block();
    }

    @Test
    @DisplayName("Delete by Id test")
    void deleteByIdTest(){
        Integer id = getInexistentId();
        LanguageDocC languageDoc = new LanguageDocC(id, "someName");

        Long previousCount = languageRepo.count().block();
        languageRepo.save(languageDoc).block();
        assertTrue(languageRepo.existsById(languageId).block());
        assertEquals(previousCount+1, languageRepo.count().block());

        languageRepo.deleteById(languageId).block();
        assertEquals(previousCount, languageRepo.count().block());
    }

    private Integer getInexistentId(){
        Integer id = languageId;
        while (languageRepo.existsById(id).block()){
            id += 1;
        }
        return id;
    }
}
