package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class DefaultApi {
    public static String getDefaultApi(String apiName) {
        OpenAPI openAPIDefaultAuth = new OpenAPI();
        openAPIDefaultAuth.setInfo(new Info()
                .title("itachallenge-"+apiName.toUpperCase()+" API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-"+apiName+" is currently unavailable!."));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(openAPIDefaultAuth);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
