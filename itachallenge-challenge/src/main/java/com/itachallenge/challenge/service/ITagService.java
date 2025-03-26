package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITagService {

    Mono<GenericResultDto<TagDto>> getAllTags();
    List<TagDocument> convertStringNameToTag(List<String> tagsAssigned);
}
