package com.itachallenge.challenge.dtos;


import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RelatedDto {


	@AllArgsConstructor
	@Getter
	@Setter
	public class LanguageDto{

	    @JsonProperty(value = "id_Related")
	    private UUID id_Related;

	}

}
