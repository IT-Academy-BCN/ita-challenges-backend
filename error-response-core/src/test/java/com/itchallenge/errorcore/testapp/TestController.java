package com.itchallenge.errorcore.testapp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/mismatch")
    public String mismatch(@RequestParam @Min(10) int age) {
        return "OK";
    }

    @PostMapping("/validate")
    public String validate(@Valid @RequestBody TestDto dto) {
        return "OK";
    }

    public static class TestDto {
        @NotNull
        public String name;
    }
}
