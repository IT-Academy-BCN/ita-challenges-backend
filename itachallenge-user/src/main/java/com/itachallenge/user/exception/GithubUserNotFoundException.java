package com.itachallenge.user.exception;

public class GithubUserNotFoundException extends RuntimeException {
  public GithubUserNotFoundException(String message) {
    super(message);
  }
}
