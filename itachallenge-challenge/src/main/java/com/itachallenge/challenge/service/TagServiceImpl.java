package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.exception.TagNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.TagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {

    private final TagRepository tagRepository;
    private final DocumentToDtoConverter<TagDocument, TagDto> tagConverter;

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


}
