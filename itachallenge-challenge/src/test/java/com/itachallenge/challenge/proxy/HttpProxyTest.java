package com.itachallenge.challenge.proxy;

import com.itachallenge.challenge.dtos.wiki.ResourceDto;
import com.itachallenge.challenge.helpers.ResourceHelper;
import io.netty.channel.ChannelOption;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class HttpProxyTest {

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
	@SneakyThrows(IOException.class)
	void requestResourcesTest(){
		MockResponse mockResponse = new MockResponse()
				.addHeader("Content-Type", "application/json")
				.setBody(new ResourceHelper(RESOURCE_JSON_PATH).readResourceAsString());
		mockWebServer.enqueue(mockResponse);

		String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
		String resourcePath = env.getProperty("url.ita_wiki.resources_paths.resources.get_resource");
		String idResourceExpected = "id_resource_as_string";
		String pathVariable = "/"+idResourceExpected;
		String url = baseUrl+resourcePath+pathVariable;
		//System.out.println("--->"+url);

		Mono<ResourceDto> response = httpProxy.getRequestData(url, ResourceDto.class);
		StepVerifier
				.create(response)
				.assertNext(resource -> assertResource(resource, idResourceExpected))
				.verifyComplete();
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

	/*
	Idem as opendata.
	Except expected response body it's not a concrete dto, due we are
	expecting an error.
	 */
	@Test
	@DisplayName("Timeout verification")
	void timeoutTest() {
		//int fakeConnectionTimeout = Integer.parseInt(env.getProperty("url.failed_connection_timeout"));
		//assertEquals(1, fakeConnectionTimeout);
		HttpClient briefHttpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1);
		WebClient briefWebClient = httpProxy.getClient().mutate()
				.clientConnector(new ReactorClientHttpConnector(briefHttpClient))
				.build();
		//System.out.println("---->"+env.getProperty("url.ds_test"));
		Mono<Object> responsePublisher = briefWebClient.get()
				.uri(env.getProperty("url.ds_test"))
				.exchangeToMono(response ->
						response.statusCode().equals(HttpStatus.OK) ?
								response.bodyToMono(Object.class) : //doesn't matter, expecting NO OK response
								response.createException().flatMap(Mono::error));
		//instead assertException + block Webclient's response:
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
}