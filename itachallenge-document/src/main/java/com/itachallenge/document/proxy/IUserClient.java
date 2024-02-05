package com.itachallenge.document.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-user", url = "http://localhost:8764")
public interface IUserClient {

    @GetMapping("/api-docs")
    String getSwaggerDocs();
}
