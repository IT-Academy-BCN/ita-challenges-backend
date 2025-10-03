package com.itachallenge.githubcore.exception;

public class GithubUnavailableException extends RuntimeException {
    public GithubUnavailableException(String message) {
        super(message);
    }

    public GithubUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
