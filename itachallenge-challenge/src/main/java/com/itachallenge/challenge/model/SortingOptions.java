package com.itachallenge.challenge.model;

import java.util.LinkedList;
import java.util.List;

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

    public static List<String> getAllValues(){
        List<String> values = new LinkedList<>();
        for(SortingOptions enumObject : SortingOptions.values()){
            values.add(enumObject.getFriendlyValue());
        }
        return values;
    }
}
