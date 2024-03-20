package com.itachallenge.document.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ApiDocsController {

    @GetMapping(value = "/api-docs/all")
    public String getApiDocs() {
        return "redirect:/api-docs";
    }
}
