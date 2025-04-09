package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.dto.TagDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Component
public class DocumentToDtoConverter<S,D> {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public Flux<D> convertDocumentFluxToDtoFlux(Flux<S> documentFlux, Class<D> dtoClass) {
        return documentFlux.map(doc -> convertDocumentToDto(doc, dtoClass));
    }

    static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public S convertDtoToDocument(D dto, Class<S> documentClass) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(dto, documentClass);
    }

    public D convertDocumentToDto(S document, Class<D> dtoClass){
        ModelMapper mapper = new ModelMapper();

        if(dtoClass.isAssignableFrom(ChallengeDto.class)) {
            Converter<LocalDateTime, String> converterFromLocalDateTimeToString = new AbstractConverter<>() {
                @Override
                protected String convert(LocalDateTime creationDateFromDocument) {
                    return creationDateFromDocument.format(CUSTOM_FORMATTER);
                }
            };
            mapper.createTypeMap(ChallengeDocument.class, ChallengeDto.class)
                    .addMapping(ChallengeDocument::getUuid, ChallengeDto::setChallengeId)
                    .addMapping(ChallengeDocument::getTitle, ChallengeDto::setTitle)
                    .addMapping(ChallengeDocument::getTimesFavorite, ChallengeDto::setTimesFavorite)
                    .addMapping(ChallengeDocument::getTimesBookmark, ChallengeDto::setTimesBookmark);
            mapper.addConverter(converterFromLocalDateTimeToString);
        }

        if(dtoClass.isAssignableFrom(LanguageDto.class)) {
            mapper.createTypeMap(LanguageDocument.class, LanguageDto.class)
                    .addMapping(LanguageDocument::getIdLanguage,LanguageDto::setLanguageId);
        }

        if(dtoClass.isAssignableFrom(TagDto.class)) {
            mapper.createTypeMap(TagDocument.class, TagDto.class)
                    .addMapping(TagDocument::getIdTag,TagDto::setTagId)
                    .addMapping(TagDocument::getTagName, TagDto::setTagName)
                    .addMapping(TagDocument::getTagDescription, TagDto::setTagDescription);
        }

        if (dtoClass.isAssignableFrom(ResourceDto.class) && document instanceof ResourceDocument) {
            mapper.createTypeMap(ResourceDocument.class, ResourceDto.class)
                    .addMapping(ResourceDocument::getResourceId, ResourceDto::setResourceId)
                    .addMapping(ResourceDocument::getTitle, ResourceDto::setTitle)
                    .addMapping(ResourceDocument::getDescription, ResourceDto::setDescription)
                    .addMapping(ResourceDocument::getUrl, ResourceDto::setUrl)
                    .addMapping(ResourceDocument::getTopic, ResourceDto::setTopic)
                    .addMapping(ResourceDocument::getContentType, ResourceDto::setContentType)
                    .addMapping(ResourceDocument::getChallengeIds, ResourceDto::setChallengeIds);
        }

        return mapper.map(document, dtoClass);
    }



}