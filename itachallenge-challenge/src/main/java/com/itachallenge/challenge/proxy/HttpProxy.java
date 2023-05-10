package com.itachallenge.challenge.proxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.config.PropertiesConfig;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class HttpProxy {

    private final PropertiesConfig config;
    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(HttpProxy.class);

    @Autowired
    public HttpProxy(PropertiesConfig config) {
        this.config = config;
        client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(initHttpClient()))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(this::initAcceptedCodecs)
                        .build())
                .build();
    }

    private HttpClient initHttpClient(){
        Integer connectionTimeout = config.getConnectionTimeout();
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(connectionTimeout))
                .compress(true)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(connectionTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(connectionTimeout, TimeUnit.MILLISECONDS)));
    }

    private void initAcceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        Integer maxBytesInMemory = config.getMaxBytesInMemory();
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        clientCodecConfigurer.defaultCodecs().maxInMemorySize(maxBytesInMemory);
        clientCodecConfigurer.customCodecs()
                .registerWithDefaultConfig(new Jackson2JsonDecoder(mapper, MediaType.TEXT_PLAIN));
    }

    //TODO: adapt method once we know which endpoint we're going to use + how use our Tag
    /*
        base url: https://dev.api.itadirectory.eurecatacademy.org
        endpoint: /api/v1/resources
        params: ?resourceType=BLOG&topic=Listas
        Response:
        {
            "resources": [
                {
                    resource 1 data
                },
                {
                    resource 2 data
                },
                etc...
            ]
        }
    */
    public <T> Mono<T> getRequestData(String url, Class<T> clazz) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS); //for testing with MockWebServer
        if (validator.isValid(url)) {
            String msg = Strings.concat("Proxy: Executing remote invocation to ",url);
            log.info(msg);
            return client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(clazz);
        } else {
            return Mono.error(new MalformedURLException("Proxy: provided url is not valid: "+url));
        }
    }

    public WebClient getClient() {
        return client;
    }
}
