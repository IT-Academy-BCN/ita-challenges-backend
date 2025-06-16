package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ITagService {

    Mono<GenericResultDto<TagDto>> getAllTags();
    Set<TagDocument> convertIdTagFromTagDocument(List<UUID> tags);
    Mono<Boolean> getValidatedTags(List<UUID> tagIds);
    Mono<GenericResultDto<TagDto>> getTagsByLanguageId(UUID languageId);
}
