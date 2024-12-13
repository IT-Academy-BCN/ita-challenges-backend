package com.itachallenge.score.util;

import lombok.Getter;

@Getter
public class MethodDetails {
    private final String beforeMethod;
    private final String methodName;
    private final String parameters;

    public MethodDetails(String beforeMethod, String methodName, String parameters) {
        this.beforeMethod = beforeMethod;
        this.methodName = methodName;
        this.parameters = parameters;
    }
}
