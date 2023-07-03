package com.itachallenge.challenge.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class RelatedDto {
	
    @JsonProperty(value = "id_related", index = 0)
    private UUID relatedId;
   
    @JsonProperty(value = "id_related_title", index = 0)
    private String titleRelatedId;
    
}