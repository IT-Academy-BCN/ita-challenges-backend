package com.itachallenge.challenge.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helper.ResourceHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class HttpProxyTest {

    @Autowired
    private Environment env;

    @Autowired
    private HttpProxy httpProxy;

    private static MockWebServer mockWebServer;

    private static final String RESOURCE_JSON_PATH = "json/resource.json";

    private static final String TOPIC_JSON_PATH = "json/topic.json";

    private static final String USER_RESOURCE_PATH = "json/user-resource.json";


    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @ParameterizedTest
    @DisplayName("GET request test")
    @MethodSource("getRequestValues")
    <T> void getRequestDataTest(String dummyJsonPath, Class<T> expectedType){
        String expectedBody = readResourceAsString(dummyJsonPath);
        T expectedObject = readResourceAsObject(dummyJsonPath, expectedType);

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(expectedBody);
        mockWebServer.enqueue(mockResponse);

        String url = String.format("http://localhost:%s", mockWebServer.getPort());
        Mono<T> response = httpProxy.getRequestData(url,expectedType);

        StepVerifier.create(response)
                .assertNext(resource ->
                        assertThat(resource).usingRecursiveComparison().isEqualTo(expectedObject))
                .verifyComplete();
    }

    private static Stream<Arguments> getRequestValues(){
        return Stream.of(
                Arguments.of(RESOURCE_JSON_PATH, ResourceTestDto.class),
                Arguments.of(TOPIC_JSON_PATH, TopicTestDto.class),
                Arguments.of(USER_RESOURCE_PATH, UserResourceTestDto.class)
        );
    }

    @Test
    @DisplayName("Timeout verification")
    void timeoutTest() {
        int absurdTimeout = Integer.parseInt(env.getProperty("url.fake_connection_timeout"));
        //System.out.println(absurdValue); // = 1
        WebClient absurdWebClient = httpProxy.getClient().mutate()
                .clientConnector(httpProxy.initReactorHttpClient(absurdTimeout))
                .build();

        String url = env.getProperty("url.ds_test"); //the same as opendata
        //System.out.println(url);
        Mono<Object> responsePublisher = absurdWebClient.get()
                .uri(url)
                .exchangeToMono(response ->
                        response.statusCode().equals(HttpStatus.OK) ?
                                response.bodyToMono(Object.class) : //doesn't matter, expecting NO OK response
                                response.createException().flatMap(Mono::error));

        StepVerifier.create(responsePublisher)
                .expectError(WebClientException.class)
                .verify();
    }

    @Test
    @DisplayName("Requesting an invalid url test")
    void providedUrlNotValidTest() {
        String wrongUrl = String.format("httKKp://localhost:%s", mockWebServer.getPort());
        String expectedErrorMsg = httpProxy.MALFORMED_URL_MSG +wrongUrl;

        Mono<Object> responsePublisher = httpProxy.getRequestData(wrongUrl, Object.class);

        StepVerifier.create(responsePublisher)
                .expectErrorMessage(expectedErrorMsg)
                .verify();
    }

    @Test
    @DisplayName("Target client is not available (500) test")
    void clientIsDownTest(){
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())); //500

        String url = String.format("http://localhost:%s", mockWebServer.getPort());
        Mono<ResourceTestDto> responsePublisher = httpProxy.getRequestData(url, ResourceTestDto.class);

        StepVerifier.create(responsePublisher)
                .expectError(WebClientException.class)
                .verify();
    }

    public static String readResourceAsString(String resourcePath){
        return new ResourceHelper(resourcePath).readResourceAsString().orElse(null);
    }

    public static <T> T readResourceAsObject(String resourcePath, Class<T> targetClass){
        String resourceAsString = readResourceAsString(resourcePath);
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(resourceAsString, targetClass);
        }catch (JsonProcessingException ex){
            return null;
        }
    }

    //inner classes only for testing purposes
    @NoArgsConstructor
    @Getter
    @Setter
    static class ResourceTestDto {
        private String id;
        private String title;
        private String slug;
        private String description;
        private String url;
        private String resourceType;
        private String createdAt;
        private String updatedAt;
        private UserResourceTestDto user;
        private List<TopicTestDto> topics;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    static class TopicTestDto {
        private String id;
        private String name;
        private String slug;
        private String categoryId;
        private String createdAt;
        private String updatedAt;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    static class UserResourceTestDto {
        private String name;
        private String email;
    }
}
