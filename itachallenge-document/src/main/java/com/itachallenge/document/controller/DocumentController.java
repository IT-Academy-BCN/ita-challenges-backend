package com.itachallenge.document.controller;

import com.fasterxml.jackson.databind.JsonNode;
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

    @GetMapping(value = "/api-docs")
/*    @Operation(operationId = "Get API docs",
            summary = "Get API docs"//,
*//*    responses = {
            @ApiResponse(responseCode = "200", description = "API docs",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JsonNode.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    }*//*)*/
    public JsonNode getApiDocs() {
        System.out.println(documentService.getSwaggerDocs());
        return documentService.getSwaggerDocs();
    }
}
