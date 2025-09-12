package com.itachallenge.user.exception;

public class GithubUserNotFoundException extends RuntimeException {
    public GithubUserNotFoundException(String username) {
        super("The username '" + username + "' is not a Github username.");
    }
}
