package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class DefaultApi {
    public static String getDefaultApi(String apiName) throws JsonProcessingException {
        OpenAPI openAPIDefaultAuth = new OpenAPI();
        openAPIDefaultAuth.setInfo(new Info()
                .title("itachallenge-Auth API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-"+apiName+" is currently unavailable!."));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultAuth);
    }
}
