package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.exception.TagNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService implements ITagService {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private DocumentToDtoConverter<TagDocument, TagDto> tagConverter = new DocumentToDtoConverter<>();

    @Cacheable(value = "allTags")
    @Override
    public Mono<GenericResultDto<TagDto>> getAllTags() {
        Flux<TagDto> tagDto = tagConverter.convertDocumentFluxToDtoFlux(tagRepository.findAll(), TagDto.class);
        return tagDto.collectList().map(tag -> {
            GenericResultDto<TagDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, tag.size(), tag.size(), tag.toArray(new TagDto[0]));
            return resultDto;
        });
    }

    @Override
    public Set<TagDocument> convertIdTagFromTagDocument(List<UUID> tagsAssigned) {
        return tagsAssigned.stream()
                .map(tag -> tagRepository.findById(tag)
                        .switchIfEmpty(Mono.error(new TagNotFoundException("Tag not found: " + tag)))
                        .block()
                )
                .collect(Collectors.toSet());
    }

    public Flux<ChallengeDocument> filterByTags(Flux<ChallengeDocument> challenges, Optional<List<UUID>> optionalTagIds) {
        if (optionalTagIds.isEmpty() || optionalTagIds.get().isEmpty()) {
            return challenges;
        }

        List<UUID> targetIds = optionalTagIds.get();

        return challenges.filter(challenge ->
                challenge.getTags() != null &&
                        challenge.getTags().stream()
                                .filter(Objects::nonNull)
                                .anyMatch(targetIds::contains)
        );

    }




}
