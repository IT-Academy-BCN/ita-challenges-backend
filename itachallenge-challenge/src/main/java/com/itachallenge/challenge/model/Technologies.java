package com.itachallenge.challenge.model;

import java.util.LinkedList;
import java.util.List;

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

    public static List<String> getAllValues(){
        List<String> values = new LinkedList<>();
        for(Technologies enumObject : Technologies.values()){
            values.add(enumObject.getFriendlyValue());
        }
        return values;
    }
}
