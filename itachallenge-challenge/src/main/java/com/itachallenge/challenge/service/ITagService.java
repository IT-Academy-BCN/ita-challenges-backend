package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ITagService {

    Mono<GenericResultDto<TagDto>> getAllTags();
    List<TagDocument> convertStringNameToTag(List<String> tagsAssigned);
    Flux<ChallengeDocument> filterByTags(Flux<ChallengeDocument> challenges, Optional<List<String>> tags);
}
