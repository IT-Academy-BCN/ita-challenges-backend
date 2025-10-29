package com.itachallenge.errorcore.testapp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/test")
public class TestController {

    Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/mismatch")
    public String mismatch(@RequestParam("age") @Min(10) int age) {
        log.debug("Made it to /mismatch path. " +
                "To trigger mismatch type, pass an unconvertible string as param; " +
                "To trigger constraint violation, pass a number smaller than 10"
        );
        return "OK";
    }

    @PostMapping("/validate")
    public String validate(@Valid @RequestBody TestDto dto) {
        log.debug("Made it to /validate path. " +
                "To trigger ArgumentMethodNotValid, pass an empty TestDto, or with a null name."
        );
        return "OK";
    }

    @GetMapping("/illegal")
    public String illegal() {
        log.debug("Made it to /illegal path. Throwing illegalArgumentException.");
        // triggers IllegalArgumentException
        throw new IllegalArgumentException("Invalid parameter passed");
    }

    @GetMapping("/status")
    public String status() {
        log.debug("Made it to /status path. Throwing ResponseStatusException.");
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Resource not found");
    }

    @GetMapping("/any")
    public String any() throws Exception {
        log.debug("Made it to /any path. Throwing Exception.");
        throw new Exception("Something went terribly wrong");
    }

    public static class TestDto {
        @NotNull(message = "name cannot be null")
        public String name;
    }
}
