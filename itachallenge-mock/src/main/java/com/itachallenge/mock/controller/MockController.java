package com.itachallenge.mock.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @RequestMapping(value = "/test")
    public String test() {
        return "Hello ITAchallenge-Mock  ;) !!!";
    }

}

