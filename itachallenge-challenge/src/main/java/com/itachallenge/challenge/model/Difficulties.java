package com.itachallenge.challenge.model;

import java.util.LinkedList;
import java.util.List;

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

    public static List<String> getAllValues(){
        List<String> values = new LinkedList<>();
        for(Difficulties enumObject : Difficulties.values()){
            values.add(enumObject.getFriendlyValue());
        }
        return values;
    }
}
