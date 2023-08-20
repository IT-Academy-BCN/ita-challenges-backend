package com.itachallenge.challenge.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorMessage {

    String msg;
    Map<String, String> errors = new HashMap<>();

    public ErrorMessage(String msg) {
        this.msg = msg;
    }

    public ErrorMessage(String msg, Map<String, String> errors) {
        this.msg = msg;
        this.errors = errors;
    }
}
