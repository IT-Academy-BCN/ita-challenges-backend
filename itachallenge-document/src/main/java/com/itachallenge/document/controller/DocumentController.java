package com.itachallenge.document.controller;

import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping
public class DocumentController {

    private final OpenApiConfig openApiConfig;
    private final DocumentService documentService;
    private Environment env;

    public DocumentController(OpenApiConfig openApiConfig, DocumentService documentService, Environment env) {
        this.openApiConfig = openApiConfig;
        this.documentService = documentService;
        this.env = env;
    }

    @GetMapping(value = "/api-docs/{apiname}", produces = {"application/json"})
    public String getSelectedOpenAPI(@PathVariable String apiname) {
        OpenAPI openAPI = openApiConfig.allOpenAPI();
        return switch (apiname) {
            case "all" -> openAPI.toString();
            case "auth" -> documentService.getSwaggerAuthDocsStr();
            case "challenge" -> documentService.getSwaggerChallengeDocsStr();
            case "user" -> documentService.getSwaggerUserDocsStr();
            default -> documentService.getSwaggerDefaultDocsStr(apiname);
        };
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getVersion() {
        String appVersion = env.getProperty("spring.application.version");
        Map<String, String> versionMap = new HashMap<>();
        versionMap.put("version", appVersion);
        return new ResponseEntity<>(versionMap, HttpStatus.OK);
    }

}
