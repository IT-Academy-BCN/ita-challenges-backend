package com.itachallenge.document.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

//TODO - Pte añadir Docker config
//@FeignClient(name = "itachallenge-challenge", url = "${MICRO_CHALLENGE_URL}")//TODO - Docker config
@FeignClient(name = "itachallenge-challenge", url = "http://localhost:8762")
public interface IChallengeClient {

    @GetMapping("/api-docs")
    String getSwaggerDocs(); //TODO - Reactive client (Feign isn't reactive)
}
