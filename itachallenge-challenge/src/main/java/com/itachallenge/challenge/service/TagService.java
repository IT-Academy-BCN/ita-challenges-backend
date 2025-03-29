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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService implements ITagService {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private DocumentToDtoConverter<TagDocument, TagDto> tagConverter = new DocumentToDtoConverter<>();

    @Cacheable(value = "allTags")
    public Mono<GenericResultDto<TagDto>> getAllTags() {
        Flux<TagDto> tagDto = tagConverter.convertDocumentFluxToDtoFlux(tagRepository.findAll(), TagDto.class);
        return tagDto.collectList().map(tag -> {
            GenericResultDto<TagDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, tag.size(), tag.size(), tag.toArray(new TagDto[0]));
            return resultDto;
        });
    }

    public List<TagDocument> convertStringNameToTag(List<String> tagsAssigned) {
        return tagsAssigned.stream()
                .map(tag -> tagRepository.findByTagName(tag)
                        .switchIfEmpty(Mono.error(new TagNotFoundException("Tag not found: " + tag)))
                        .block()
                )
                .collect(Collectors.toList());
    }

    @Override
    public Flux<ChallengeDocument> filterByTags(Flux<ChallengeDocument> challenges, Optional<List<String>> tags) {
        if (tags.isPresent() && tags.get().stream().anyMatch(tag -> tag != null && !tag.isBlank())) {
            List<String> tagList = tags.get().stream()
                    .filter(tag -> tag != null && !tag.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            return challenges.filter(challenge -> {
                if (challenge.getTags() == null || challenge.getTags().isEmpty()) {
                    return false;
                }

                List<String> challengeTagNames = challenge.getTags().stream()
                        .map(TagDocument::getTagName)
                        .filter(Objects::nonNull)
                        .map(String::toLowerCase)
                        .toList();

                return challengeTagNames.stream().anyMatch(tagList::contains);
            });
        }

        return challenges;
    }










}
