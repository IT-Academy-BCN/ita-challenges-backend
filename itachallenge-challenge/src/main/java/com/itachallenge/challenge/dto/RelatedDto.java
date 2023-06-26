package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RelatedDto {
	
    @JsonProperty(value = "id_related", index = 0)
    private String relatedId;
   
    
}
