package com.itachallenge.challenge.model;

public enum Technologies {

    JAVASCRIPT("Javascript"),
    JAVA("Java"),
    PHP("PHP"),
    PYTHON("Python");

    private final String value;

    Technologies(String value) {
        this.value = value;
    }

    public String getFriendlyValue(){
        return value;
    }
}
