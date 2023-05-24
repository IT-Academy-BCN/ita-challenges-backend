package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ResourceDto { //for challenge's resources section

    //Note: data provided by ITA-WIKI

    //Usar/Ampliar constructores / setters según necesidad
    //Canviar types en caso que facilite inicializar el objeto
    @JsonProperty("id_resource")
    private String resourceId;
    @JsonProperty("resource_title")
    private String title;
    @JsonProperty("reosurce_description")
    private String description;
    private String url;
    @JsonProperty("author")
    private String userPoster; //the user who posted the resource in ITA-WIKI
    @JsonProperty("generation_date")
    private String generationDate; //when the resource was posted in ITA-WIKI

    //TODO: check figma. Seems we need to allow to vote for each resource listed
    private String votes; //num of votes (indicates the "value" of the resource)
}
