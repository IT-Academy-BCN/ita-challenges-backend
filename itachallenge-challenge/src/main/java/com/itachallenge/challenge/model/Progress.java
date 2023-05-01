package com.itachallenge.challenge.model;

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
}
