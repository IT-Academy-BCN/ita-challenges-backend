package com.itachallenge.challenge.proxy;

import com.itachallenge.challenge.dtos.itawiki.ResourceDto;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class HttpProxyTest {

    @Autowired
    private Environment env;

    @Autowired
    private HttpProxy httpProxy;

    private static MockWebServer mockWebServer;

    private final String RESOURCE_JSON_PATH = "json/OneResource.json";


    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("GET request test")
    void requestResourcesTest(){
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(this.readResourceAsString(RESOURCE_JSON_PATH));
        mockWebServer.enqueue(mockResponse);

        String idResourceExpected = "id_resource_as_string";
        String url = initUrlGetResource(idResourceExpected);

        Mono<ResourceDto> response = httpProxy.getRequestData(url, ResourceDto.class);
        StepVerifier
                .create(response)
                .assertNext(resource -> assertResource(resource, idResourceExpected))
                .verifyComplete();
    }

    private String initUrlGetResource(String idResource){
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        String resourceEndpoint = env.getProperty("url.ita_wiki.resources_paths.resources.get_resource");
        String pathVariable = "/"+idResource;
        String url = baseUrl+resourceEndpoint+pathVariable;
        //System.out.println("--->"+url);
        return url;
    }

    private void assertResource(ResourceDto resource, String idResource){
        assertThat(resource.getId(), equalTo(idResource));
        assertThat(resource.getTitle(),is(not(emptyString())));
        assertThat(resource.getSlug(),is(not(emptyString())));
        assertThat(resource.getDescription(),is(not(emptyString())));
        assertThat(resource.getUrl(),is(not(emptyString())));
        assertThat(resource.getResourceType(),is(not(emptyString())));
        assertThat(resource.getCreatedAt(),is(not(emptyString())));
        assertThat(resource.getTopics(), everyItem(is(notNullValue())));
        assertThat(resource.getUser().getName(),is(not(emptyString())));
    }

    @Test
    @DisplayName("Timeout verification")
    public void timeoutTest() {
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
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())); //500
        String url = String.format("http://localhost:%s", mockWebServer.getPort());
        Mono<ResourceDto> responsePublisher = httpProxy.getRequestData(url, ResourceDto.class);
        StepVerifier.create(responsePublisher)
                .expectError(WebClientException.class)
                .verify();
    }

    public static String readResourceAsString(String resourcePath){
        try {
            File file = new ClassPathResource(resourcePath).getFile();
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return ""; //if no file in path or IOException when read -> Empty String
        }

        /*
        Code in ResourceHelper:

        Resource resource = new ClassPathResource(this.resourcePath);

        public Optional<String> readResourceAsString (){
            Optional<String> result = null;
            try {
                result = Optional.of(FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                log.error(getResourceErrorMessage("loading/reading").concat(ex.getMessage()));
            }
            return result;
        }

        //IF OIException -> return result <-> return null <-> Optional<String> result = null;
        //ELSE (No IOException) -> return result <-> return Optional.of(file read as String) <-> never null or empty
         */
    }
}
