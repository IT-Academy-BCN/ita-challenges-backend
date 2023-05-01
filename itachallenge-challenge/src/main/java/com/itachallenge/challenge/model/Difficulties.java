package com.itachallenge.challenge.model;

public enum Difficulties {

    EASY("Fácil"),
    MEDIUM("Media"),
    HARD("Difícil");

    private final String value;

    Difficulties(String value) {
        this.value = value;
    }

    public String getFriendlyValue(){
        return value;
    }
}
