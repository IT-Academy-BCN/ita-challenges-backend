package com.itachallenge.document.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-score", url = "http://localhost:8763")
public interface IScoreClient {

    @GetMapping("/api-docs")
    String getSwaggerDocs();
}
