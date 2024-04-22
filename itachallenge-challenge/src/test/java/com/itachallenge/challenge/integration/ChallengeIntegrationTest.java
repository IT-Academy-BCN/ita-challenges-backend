package com.itachallenge.challenge.integration;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.ExampleDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ChallengeIntegrationTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("challenges"));
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ChallengeRepository challengeRepository;

    private final String UUID_VALID = "8ecbfe54-fec8-11ed-be56-0242ac120002";
    private final String UUID_INVALID = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @BeforeEach
    public void setUp() {

        Set<UUID> UUIDSet = new HashSet<>(Arrays.asList(uuid_2, uuid_1));
        Set<UUID> UUIDSet2 = new HashSet<>(Arrays.asList(uuid_2, uuid_1));
        Map<Locale, String> exampleMap1 = new HashMap<>();
            exampleMap1.put(Locale.forLanguageTag("ES"), "Ejemplo texto en español");
            exampleMap1.put(Locale.forLanguageTag("CA"), "Exemple texte en català");
            exampleMap1.put(Locale.ENGLISH, "Example text in english");
        Map<Locale, String> exampleMap2 = new HashMap<>();
            exampleMap2.put(Locale.forLanguageTag("ES"), "Ejemplo texto random en español");
            exampleMap2.put(Locale.forLanguageTag("CA"), "Exemple texte random en català");
            exampleMap2.put(Locale.ENGLISH, "Random example in english");

        ExampleDocument example = new ExampleDocument(uuid_1, exampleMap1);
        ExampleDocument example2 = new ExampleDocument(uuid_2, exampleMap2);
        List<ExampleDocument> exampleList = new ArrayList<>(Arrays.asList(example2, example));

        UUID uuidLang1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuidLang2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        UUID[] idsLanguages = new UUID[]{uuidLang1, uuidLang2};
        String[] languageNames = new String[]{"name1", "name2"};
        LanguageDocument language1 = getLanguageMocked(idsLanguages[0], languageNames[0]);
        LanguageDocument language2 = getLanguageMocked(idsLanguages[1], languageNames[1]);
        Set<LanguageDocument> languageSet = Set.of(language1, language2);

        List<UUID> solutionList = List.of(UUID.randomUUID(), UUID.randomUUID());
        Map<Locale, String> descriptionMap = new HashMap<>();
            descriptionMap.put(Locale.forLanguageTag("ES"), "Descripción en español");
            descriptionMap.put(Locale.forLanguageTag("CA"), "Descripció en català");
            descriptionMap.put(Locale.ENGLISH, "Description in english");
        Map<Locale, String> notesMap = new HashMap<>();
            notesMap.put(Locale.forLanguageTag("ES"), "Detail note en español");
            notesMap.put(Locale.forLanguageTag("CA"), "Detail note en català");
            notesMap.put(Locale.ENGLISH, "Detail note in english");

        DetailDocument detail = new DetailDocument(descriptionMap, exampleList, notesMap);

        Map<Locale, String> title1 = new HashMap<>();
            title1.put(Locale.forLanguageTag("ES"), "Loops");
            title1.put(Locale.forLanguageTag("CA"), "Loops");
            title1.put(Locale.ENGLISH, "Loops");
        Map<Locale, String> title2 = new HashMap<>();
            title2.put(Locale.forLanguageTag("ES"), "If");
            title2.put(Locale.forLanguageTag("CA"), "If");
            title2.put(Locale.ENGLISH, "If");

        ChallengeDocument challenge = new ChallengeDocument
                (uuid_1, title1, "Level 1", LocalDateTime.now(), detail, languageSet, solutionList, UUIDSet, UUIDSet2);
        ChallengeDocument challenge2 = new ChallengeDocument
                (uuid_2, title2, "Level 2", LocalDateTime.now(), detail, languageSet, solutionList, UUIDSet, UUIDSet2);

        challengeRepository.saveAll(Flux.just(challenge, challenge2)).blockLast();
    }

    //TODO - Refactor this method, getLanguages endpoint already available
    private LanguageDocument getLanguageMocked(UUID idLanguage, String languageName) {
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

    @Test
    @DisplayName("Test response Hello")
    void testDevProfile_OKWithoutAuthentication() {
        webTestClient
                .get()
                .uri("/itachallenge/api/v1/challenge/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(String::toString, equalTo("Hello from ITA Challenge!!!"));
    }

    @Test
    void shouldReturnBadRequestForUnknownUserId() { //TODO Change to proper HTTP status once controller has been normalized
        webTestClient
                .get()
                .uri(CHALLENGE_BASE_URL + "/challenges/{challengeId}", UUID_INVALID)
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldReturnOk_ValidUserId() {
        webTestClient
                .get()
                .uri(CHALLENGE_BASE_URL + "/challenges/{challengeId}", UUID_VALID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChallengeDto.class)
                .value(dto -> {
                    assert dto != null;
                });
    }

    @Test
    void getChallengesByPages_ValidPageParameters_ChallengesReturned() {
        webTestClient
                .get()
                .uri("/itachallenge/api/v1/challenge/challenges?offset=0&limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class)
                .contains(new ChallengeDto[]{})
                .hasSize(1);
    }

    @Test
    void getChallengesByPages_NullPageParameters_ChallengesReturned() {
        webTestClient
                .get()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class)
                .contains(new ChallengeDto[]{})
                .hasSize(2);
    }

    @Test
    void removeResourcesById_ValidId_ResourceDeleted() {
        webTestClient
                .delete()
                .uri("/itachallenge/api/v1/challenge/resources/{idResource}", UUID_VALID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(responseMap -> {
                    String response = (String) responseMap.get("response");
                    assert response.equals("Resource removed successfully");
                });
    }

}