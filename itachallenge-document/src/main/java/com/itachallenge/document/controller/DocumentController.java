package com.itachallenge.document.controller;

import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping
public class DocumentController {

    @Value("${version}")
    private String version;

    private final OpenApiConfig openApiConfig;
    private final DocumentService documentService;

    public DocumentController(OpenApiConfig openApiConfig, DocumentService documentService) {
        this.openApiConfig = openApiConfig;
        this.documentService = documentService;
    }

    @GetMapping(value = "/api-docs/{apiname}", produces = {"application/json"})
    public String getSelectedOpenAPI(@PathVariable String apiname) {
        OpenAPI openAPI = openApiConfig.allOpenAPI();
        return switch (apiname) {
            case "auth" -> documentService.getSwaggerAuthDocsStr();
            case "challenge" -> documentService.getSwaggerChallengeDocsStr();
            case "score" -> documentService.getSwaggerScoreDocsStr();
            case "user" -> documentService.getSwaggerUserDocsStr();
            default -> openAPI != null ? openAPI.toString() : "";
        };
    }

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("Application version: "+version);
    }

}
