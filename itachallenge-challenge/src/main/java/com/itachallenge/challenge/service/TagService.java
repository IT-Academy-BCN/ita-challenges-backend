package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TagService {

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


}
