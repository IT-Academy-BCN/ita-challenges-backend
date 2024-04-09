package com.itachallenge.document.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping
public class DocumentController {

    private final OpenApiConfig openApiConfig;
    private final DocumentService documentService;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    public DocumentController(OpenApiConfig openApiConfig, DocumentService documentService) {
        this.openApiConfig = openApiConfig;
        this.documentService = documentService;
    }

    @GetMapping(value = "/api-docs/{apiname}", produces = {"application/json"})
    public String getSelectedOpenAPI(@PathVariable String apiname) {
        OpenAPI openAPI = openApiConfig.allOpenAPI();
        return switch (apiname) {
            case "all" -> openAPI.toString();
            case "auth" -> documentService.getSwaggerAuthDocsStr();
            case "challenge" -> documentService.getSwaggerChallengeDocsStr();
            case "score" -> documentService.getSwaggerScoreDocsStr();
            case "user" -> documentService.getSwaggerUserDocsStr();
            default -> documentService.getSwaggerDefaultDocsStr(apiname);
        };
    }

    @GetMapping(value = "/api-docs")
    public JsonNode getApiDocs() {
        System.out.println(documentService.getSwaggerDocs());
        return documentService.getSwaggerDocs();
    }

    @GetMapping("/version")
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }
}
