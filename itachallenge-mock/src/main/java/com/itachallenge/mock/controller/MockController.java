package com.itachallenge.mock.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @Value("${version}")
    private String version;

    @RequestMapping(value = "/test")
    public String test() {
        return "Hello ITAchallenge-Mock  ;) !!!";
    }

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("Application version: "+version);
    }

}

