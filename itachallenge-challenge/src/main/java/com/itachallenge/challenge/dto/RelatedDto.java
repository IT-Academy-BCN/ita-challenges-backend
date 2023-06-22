package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedDto {
	

    @JsonProperty(value = "id_related", index = 0)
    private String relatedId;


	public RelatedDto(String relatedId) {

		this.relatedId = relatedId;
	}
    
    
}
