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

    private static final Logger log = LoggerFactory.getLogger(HttpProxy.class);

    private final PropertiesConfig config;

    private final WebClient client;

    protected static final String MALFORMED_URL_MSG = "Proxy: provided url is not valid: ";

    @Autowired
    public HttpProxy(PropertiesConfig config) {
        this.config = config;
        client = WebClient.builder()
                .clientConnector(initReactorHttpClient(config.getConnectionTimeout()))
                .exchangeStrategies(initExchangeStrategies())
                .build();
    }

    //protected because it's used in test (timeout verification test)
    protected ReactorClientHttpConnector initReactorHttpClient(Integer connectionTimeout){
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(connectionTimeout))
                .compress(true)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(connectionTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(connectionTimeout, TimeUnit.MILLISECONDS)));
        return new ReactorClientHttpConnector(httpClient);
    }

    private ExchangeStrategies initExchangeStrategies(){
        return ExchangeStrategies.builder()
                .codecs(this::initAcceptedCodecs)
                .build();
    }

    private void initAcceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        Integer maxBytesInMemory = config.getMaxBytesInMemory();
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        clientCodecConfigurer.defaultCodecs().maxInMemorySize(maxBytesInMemory);
        clientCodecConfigurer.customCodecs()
                .registerWithDefaultConfig(new Jackson2JsonDecoder(mapper, MediaType.TEXT_PLAIN));
    }

    public <T> Mono<T> getRequestData(String url, Class<T> clazz) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS); //allow localhost
        if (validator.isValid(url)) {
            String msg = Strings.concat("Proxy: Executing remote invocation to ",url);
            log.info(msg);
            return client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(clazz);
        } else {
            return Mono.error(new MalformedURLException(MALFORMED_URL_MSG +url));
        }
    }

    //protected because it's used in test (timeout verification test)
    protected WebClient getClient() {
        return client;
    }
}
