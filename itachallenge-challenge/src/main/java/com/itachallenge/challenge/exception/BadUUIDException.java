package com.itachallenge.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class BadUUIDException extends Exception {
    final String message;
}
