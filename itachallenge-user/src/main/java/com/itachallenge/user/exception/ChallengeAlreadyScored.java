package com.itachallenge.user.exception;

public class ChallengeAlreadyScored extends RuntimeException {
    public ChallengeAlreadyScored(String message)
    {
        super(message);
    }
}
