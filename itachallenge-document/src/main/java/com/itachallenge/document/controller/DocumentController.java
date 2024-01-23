package com.itachallenge.document.controller;

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
    public String test() {
        log.info("** Saludos desde el logger **");

        return "Hello from ITA Challenge Document!!!";
    }

}
