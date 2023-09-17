package com.itachallenge.challenge.dto;

import com.itachallenge.challenge.document.DetailDocument;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeDto{

    private UUID uuid;

    private String title;

    private String level;

    private String creationDate;

    private DetailDocument detail;

    // Info a obtener del micro score
    private Integer popularity;

    // Info a obtener del micro score
    private Float percentage;

    private Set<LanguageDto> languages;

    private List<UUID> solutions;

    private Set<UUID> resources;

    private Set<UUID> relatedChallenges;

    /*
    TODO: ADD more fields "on demand" (when needed)
     */
}
