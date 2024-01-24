package com.itachallenge.document.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

//TODO - Pte añadir Docker config
//@FeignClient(name = "itachallenge-challenge", url = "${MICRO_CHALLENGE_URL}")
@FeignClient(name = "itachallenge-challenge", url = "http://localhost:8762")
public interface IChallengeClient {

    @GetMapping("/api-docs")
    Mono<String> getSwaggerDocs();
}
