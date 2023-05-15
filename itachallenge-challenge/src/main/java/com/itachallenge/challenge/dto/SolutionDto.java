package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SolutionDto { //for challenge's solution section

    //Usar/Ampliar constructores / setters según necesidad
    //Canviar types en caso que facilite inicializar el objeto
    @JsonProperty("id_solution")
    private String solutionId;
    @JsonProperty("solution_text")
    private String code;
}
