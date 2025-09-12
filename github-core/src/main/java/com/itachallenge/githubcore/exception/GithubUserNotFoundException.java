package com.itachallenge.githubcore.exception;

public class GithubUserNotFoundException extends RuntimeException {
    public GithubUserNotFoundException(String username) {
        super("GitHub user not found: " + username);
    }
}
