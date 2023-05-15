package com.itachallenge.challenge.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DetailDto { //for challenge's details section

    //Usar/Ampliar constructores / setters según necesidad
    //Canviar typesen caso que facilite inicializar el objeto
    private String description;
    private List<ExampleDto> examples;
    private String notes;
}
