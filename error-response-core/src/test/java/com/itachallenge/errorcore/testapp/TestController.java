package com.itachallenge.errorcore.testapp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/mismatch")
    public String mismatch(@RequestParam("age") @Min(10) int age) {
        return "OK";
    }

    @PostMapping("/validate")
    public String validate(@Valid @RequestBody TestDto dto) {
        return "OK";
    }

    @GetMapping("/illegal")
    public String illegal() {
        // triggers IllegalArgumentException
        throw new IllegalArgumentException("Invalid parameter passed");
    }

    @GetMapping("/status")
    public String status() {
        // triggers ResponseStatusException (404)
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Resource not found");
    }

    @GetMapping("/any")
    public String any() throws Exception {
        // triggers generic Exception
        throw new Exception("Something went terribly wrong");
    }

    public static class TestDto {
        @NotNull(message = "name cannot be null")
        public String name;
    }
}
