package com.itachallenge.document.controller;

import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@Validated
@RequestMapping
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

/*    @GetMapping(value = "/test")
    @Operation(
            operationId = "Test",
            summary = "Only for testing purposes",
            description = "Test",
            responses = {@ApiResponse(responseCode = "200")})
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge Document!!!";
    }*/


    @GetMapping(value = "/api-docs")
    public Mono<String> getApiDocs() {
        return documentService.getSwaggerDocs();
    }

}
