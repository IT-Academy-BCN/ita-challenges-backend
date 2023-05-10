package com.itachallenge.challenge.proxy;

import com.itachallenge.challenge.dto.wiki.WikiResourceDto;
import com.itachallenge.challenge.dto.wiki.WikiResourcesDto;
import io.netty.channel.ChannelOption;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@PropertySource("classpath:application-test.yml")
class HttpProxyTest {

	@Autowired
	private HttpProxy httpProxy;
	private static MockWebServer mockWebServer;
	private final String RESOURCES_JSON_PATH = "json/2Resources.json";


	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

	}

	//@MockBean
	//private PropertiesConfig config;

	/*
		A) No se puede poner el codigo en @BeforeAll (con ProxyConfig static).
			-> Debido a que config is null.
		B) Tanto si indico el codigo a mockear en @BeforeEach como en el @Test:
		org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 0
		-> 	el client NO se configura correctamente (cuando, en el costructor, se llama al config
		C) Añadiendo el application-test.yml sigue dando el mismo error
	*/

	/*
	@BeforeEach
	void init(){
		when(config.getConnectionTimeout()).thenReturn(30000);
		when(config.getMaxBytesInMemory()).thenReturn(30000000);
	}
	 */


	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	@DisplayName("GET request test")
	@SneakyThrows(IOException.class)
	void getRequestDataURLTest(){
		MockResponse mockResponse = new MockResponse()
				.addHeader("Content-Type", "application/json")
				.setBody(readResourceAsString(RESOURCES_JSON_PATH));
		mockWebServer.enqueue(mockResponse);

		//TODO: remove hardcoded paths once we know where + how request resources to ITA-WIKI
		String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
		String resourcePath = "/api/v1/resources";
		String type = "BLOG";
		String topic = "Listas";
		String resourceParams = "?resourceType="+type+"&topic="+topic;
		String url = Strings.concat(baseUrl,resourcePath,resourceParams);

		Mono<WikiResourcesDto> response = httpProxy.getRequestData(url, WikiResourcesDto.class);
		StepVerifier
				.create(response)
				.assertNext(resourcesDto -> {
					assertEquals(2, resourcesDto.getResources().size());
					for(WikiResourceDto resource : resourcesDto.getResources()){
						assertResource(resource, type, topic);
					}
				})
				.verifyComplete();
	}

	private void assertResource(WikiResourceDto resource, String type, String topic){
		assertThat(resource.getId(), is(not(emptyString())));
		assertThat(resource.getTitle(),is(not(emptyString())));
		assertThat(resource.getSlug(),is(not(emptyString())));
		assertThat(resource.getDescription(),is(not(emptyString())));
		assertThat(resource.getUrl(),is(not(emptyString())));
		assertThat(resource.getResourceType(),equalTo(type));
		assertThat(resource.getCreatedAt(),is(not(emptyString())));
		assertThat(resource.getTopics(), hasItem(hasProperty("name",equalTo(topic))));
		assertThat(resource.getUser().getName(),is(not(emptyString())));
	}

	@Test
	@DisplayName("Timeout verification")
	void timeoutTest() {
		HttpClient briefHttpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1); // Absurd 1 ms connection timeout
		WebClient briefWebClient = httpProxy.getClient().mutate()
				.clientConnector(new ReactorClientHttpConnector(briefHttpClient))
				.build();
		String randomUri = "https://github.com/";
		Mono<Object> responsePublisher = briefWebClient.get()
				.uri(randomUri)
				.exchangeToMono(response ->
						response.statusCode().equals(HttpStatus.OK) ?
								response.bodyToMono(Object.class) :
								response.createException().flatMap(Mono::error));
		StepVerifier.create(responsePublisher)
				.expectError(WebClientException.class)
				.verify();
	}

	@Test
	@DisplayName("Requesting an invalid url test")
	void providedUrlNotValidTest() {
		String wrongUrl = String.format("httKKp://localhost:%s", mockWebServer.getPort());
		Mono<Object> responsePublisher = httpProxy.getRequestData(wrongUrl, Object.class);
		StepVerifier.create(responsePublisher)
						.expectErrorMessage("Proxy: provided url is not valid: "+wrongUrl)
						.verify();
	}

	@Test
	@DisplayName("Target client is not available (500) test")
	void clientIsDownTest(){
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		String url = String.format("http://localhost:%s", mockWebServer.getPort());
		Mono<WikiResourcesDto> responsePublisher = httpProxy.getRequestData(url, WikiResourcesDto.class);
		StepVerifier.create(responsePublisher)
						.expectError(WebClientException.class)
				        .verify();
	}



	//TODO: replace method. Use new ResourceHelper().readResourceAsString(path) instead
	// + remove @SneakyThrows in invoker
	public static String readResourceAsString (String resourcePath)  throws IOException{
		Resource resource = new ClassPathResource(resourcePath);
		File file = resource.getFile();
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);

	}
}