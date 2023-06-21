package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedDto {
	

    @JsonProperty(value = "id_related", index = 0)
    private String relatedId;

    @JsonProperty(value = "related_name", index = 1)
    private String relatedName;

	public RelatedDto(String relatedId, String relatedName) {
		super();
		this.relatedId = relatedId;
		this.relatedName = relatedName;
	}
    
    
}
