package com.itachallenge.challenge.dto.wiki;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class WikiResourcesDto {

    private List<WikiResourceDto> resources;
}
