package com.itachallenge.document.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-auth", url = "http://localhost:8761")
public interface IAuthClient {

    @GetMapping("/api-docs")
    String getSwaggerDocs();
}
