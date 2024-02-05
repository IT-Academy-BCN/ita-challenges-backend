package com.itachallenge.document.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping
public class DocumentController {


    private final OpenApiConfig openApiConfig;
    public DocumentController(OpenApiConfig openApiConfig) {
        this.openApiConfig = openApiConfig;
    }
    @GetMapping(value = "/api-docs/all",produces = {"application/json"})
    @ResponseBody
    public OpenAPI getAllOpenAPI() throws JsonProcessingException {
        return openApiConfig.allOpenAPI();
    }

}
