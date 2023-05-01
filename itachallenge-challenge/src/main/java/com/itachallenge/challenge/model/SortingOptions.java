package com.itachallenge.challenge.model;

public enum SortingOptions {

    POPULARITY("Popularidad"),
    DATE("Fecha");

    private final String value;

    SortingOptions(String value) {
        this.value = value;
    }

    public String getFriendlyValue(){
        return value;
    }
}
