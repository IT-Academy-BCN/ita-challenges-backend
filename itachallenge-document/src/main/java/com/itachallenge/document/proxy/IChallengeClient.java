package com.itachallenge.document.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-challenge", url = "http://localhost:8762")
public interface IChallengeClient {

    @GetMapping("/api-docs")
    String getSwaggerDocs();
}
