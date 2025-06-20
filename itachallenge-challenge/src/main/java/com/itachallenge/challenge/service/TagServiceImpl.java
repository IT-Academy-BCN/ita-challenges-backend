package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.TagNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements ITagService {

    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private DocumentToDtoConverter<TagDocument, TagDto> tagConverter = new DocumentToDtoConverter<>();

    @Cacheable(value = "tagsByLanguage")
    @Override
    public Mono<GenericResultDto<TagDto>> getTagsByLanguageId(UUID languageId) {
        if (languageId == null) {
            log.warn("[TagService] languageId is null");
            return Mono.error(new IllegalArgumentException("languageId cannot be null"));
        }

        return tagConverter.convertDocumentFluxToDtoFlux(tagRepository.findByLanguageId(languageId), TagDto.class)
                .doOnError(e -> log.error("[TagService] Error converting documents to DTO:", e))
                .collectList()
                .doOnError(e -> log.error("[TagService] Error collecting tag list:", e))
                .map(tagList -> {
                    log.debug("[TagService] Total tags found: {}", tagList.size());
                    GenericResultDto<TagDto> resultDto = new GenericResultDto<>();
                    resultDto.setInfo(0, tagList.size(), tagList.size(), tagList.toArray(new TagDto[0]));
                    return resultDto;
                })
                .doOnError(e -> log.error("[TagService] Error mapping final result:", e));
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
    
    @Override
    public Mono<Boolean> getValidatedTags(List<UUID> tagIds) {
        return validateNoDuplicatesUUIDTags(tagIds).
                then(validateAllUUIDTagsExist(tagIds));
    }
    
    private Mono<Boolean> validateNoDuplicatesUUIDTags(List<UUID> tagIds) {
        return Flux.fromIterable(tagIds)
                .groupBy(id -> id)
                .flatMap(group -> group.count()
                        .filter(cnt -> cnt > 1)
                        .map(cnt -> group.key()))
                .next()
                .flatMap(dup ->
                        Mono.error(new BadRequestException("tag UUID duplicated: " + dup))
                )
                .hasElement();
    }
    
    private Mono<Boolean> validateAllUUIDTagsExist(List<UUID> tagIds) {
        return Flux.fromIterable(tagIds)
                .flatMap(tagId -> tagRepository.findById(tagId)
                        .switchIfEmpty(Mono.error(new TagNotFoundException("Tag not found: " + tagId))))
                .count()
                .map(count -> count == tagIds.size())
                .hasElement();
    }
}
