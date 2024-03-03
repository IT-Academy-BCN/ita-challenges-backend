package com.itachallenge.user.exception;

public class NullException extends Exception{
    public NullException(String nullGetChallengeUsersPercentage) {
        super("null at get Challenge Users Percentage");
    }
}
