package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DocumentToDtoConverter<S,D> {

    public Flux<D> convertDocumentFluxToDtoFlux(Flux<S> documentFlux, Class<D> dtoClass) {
        return documentFlux.map(doc -> convertDocumentToDto(doc, dtoClass));
    }

    static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
                    .addMapping(ChallengeDocument::getTitle, ChallengeDto::setTitle);
            mapper.addConverter(converterFromLocalDateTimeToString);
        }

        if(dtoClass.isAssignableFrom(LanguageDto.class)) {
            mapper.createTypeMap(LanguageDocument.class, LanguageDto.class)
                    .addMapping(LanguageDocument::getIdLanguage,LanguageDto::setLanguageId);
        }

        return mapper.map(document, dtoClass);
    }


        if (dtoClass.isAssignableFrom(ResourceDto.class) && document instanceof ResourceDocument) {
            // Aquí afegim només el mapeig específic per a ResourceDocument i ResourceDto
            modelMapper.createTypeMap(ResourceDocument.class, ResourceDto.class)
                    .addMapping(ResourceDocument::getUuid, ResourceDto::setResourceId)
                    .addMapping(ResourceDocument::getTitle, ResourceDto::setTitle)
                    .addMapping(ResourceDocument::getDescription, ResourceDto::setDescription)
                    .addMapping(ResourceDocument::getUrl, ResourceDto::setUrl)
                    .addMapping(ResourceDocument::getTopic, ResourceDto::setTopic)
                    .addMapping(ResourceDocument::getContentType, ResourceDto::setContentType)
                    .addMapping(ResourceDocument::getChallengeIds, ResourceDto::setChallengeIds);
        }

        // Continuar amb la conversió genèrica per a qualsevol altre tipus de document
        return modelMapper.map(document, dtoClass);
    }



