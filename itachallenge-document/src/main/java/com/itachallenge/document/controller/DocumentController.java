package com.itachallenge.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/document")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);


    @GetMapping(value = "/test")
    @Operation(
            operationId = "Test",
            summary = "Only for testing purposes",
            description = "Test",
            responses = {@ApiResponse(responseCode = "200")})
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge Document!!!";
    }

}
