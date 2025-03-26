package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import reactor.core.publisher.Mono;

public interface ITagService {

    Mono<GenericResultDto<TagDto>> getAllTags();
}
