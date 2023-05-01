package com.itachallenge.challenge.model;

import java.util.LinkedList;
import java.util.List;

public enum Progress {

    NOT_STARTED("No empezados"),
    NOT_COMPLETED("Falta completar"),
    COMPLETED("Completados");

    private final String value;

    Progress(String value) {
        this.value = value;
    }

    public String getFriendlyValue(){
        return value;
    }

    public static List<String> getAllValues(){
        List<String> values = new LinkedList<>();
        for(Progress enumObject : Progress.values()){
            values.add(enumObject.getFriendlyValue());
        }
        return values;
    }
}
