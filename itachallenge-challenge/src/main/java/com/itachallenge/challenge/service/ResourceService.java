package com.itachallenge.challenge.service;
import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.ChallengeListDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.enums.AssociationType;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ResourceRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceService implements IResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);
    private final ResourceRepository resourceRepository;
    private final DocumentToDtoConverter<ResourceDocument, ResourceDto> resourceConverter;
    private final IChallengeService challengeService;
    private final ModelMapper mapper;

    public ResourceService(ResourceRepository resourceRepository, DocumentToDtoConverter<ResourceDocument,
            ResourceDto> resourceConverter, IChallengeService challengeService, ModelMapper mapper) {
        this.resourceRepository = resourceRepository;
        this.resourceConverter = resourceConverter;
        this.challengeService = challengeService;
        this.mapper = mapper;
    }

    @CacheEvict(value = "resources", allEntries = true)
    @Override
    public Mono<ResourceDto> createResource(ResourceDto resourceDto) {
        log.info("Creating resource for the topic {}", resourceDto.getTopic());

        if (resourceDto.getContentType() == null) {
            return Mono.error(new IllegalArgumentException("Content type is required"));
        }
        if (resourceDto.getTopic() == null) {
            return Mono.error(new IllegalArgumentException("Topic is required"));
        }

        Topic topic = Topic.fromDisplayName(resourceDto.getTopic().toString());
        resourceDto.setTopic(topic);

        if (resourceDto.getAssociationType() == AssociationType.NONE) {
            return saveResource(resourceDto);
        }

        if (resourceDto.getAssociationType() == AssociationType.CHOOSE) {
            return challengeService.getChallengesByTopic(topic, 0, -1)
                    .defaultIfEmpty(new ChallengeListDto(Collections.emptyList(), 0))
                    .doOnNext(challengeList -> log.info("ChallengeListDo received {}", challengeList))
                    .flatMap(challengeResult -> {
                        List<ChallengeDto> matchingChallenges = challengeResult.getResults() != null
                                ? challengeResult.getResults()
                                : new ArrayList<>();

                        if (matchingChallenges.isEmpty()) {
                            return Mono.error(new IllegalArgumentException("No challenges found for the selected topic"));
                        }

                        return Mono.just(ResourceDto.builder()
                                .resourceId(resourceDto.getResourceId())
                                .title(resourceDto.getTitle())
                                .description(resourceDto.getDescription())
                                .url(resourceDto.getUrl())
                                .topic(resourceDto.getTopic())
                                .contentType(resourceDto.getContentType())
                                .associationType(resourceDto.getAssociationType())
                                .challengeIds(matchingChallenges.stream()
                                        .map(ChallengeDto::getChallengeId)
                                        .collect(Collectors.toList()))
                                .build());
                    });
        }


        return Optional.ofNullable(challengeService.getChallengesByTopic(topic, 0, -1))
                .orElse(Mono.just(new ChallengeListDto(Collections.emptyList(), 0)))
                .doOnNext(challengeList -> log.info("ChallengeListDto received {}", challengeList))
                .flatMap(challengeResult -> {
                    List<ChallengeDto> matchingChallenges = challengeResult.getResults() != null
                            ? challengeResult.getResults()
                            : new ArrayList<>();

                    if (!matchingChallenges.isEmpty()) {
                        resourceDto.setChallengeIds(matchingChallenges.stream()
                                .map(ChallengeDto::getChallengeId)
                                .collect(Collectors.toList()));
                    }
                    return saveResource(resourceDto);
                })
                .switchIfEmpty(saveResource(resourceDto))
                .doOnError(error -> log.error("Error creating resource {}", error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Handling error {}", error.getMessage());
                    return Mono.error(new RuntimeException("Error creating resource"));
                });
    }


    private Mono<ResourceDto> saveResource(ResourceDto resourceDto) {
        if (resourceDto == null) {
            log.error("Error where resourceDto null!");
            return Mono.error(new IllegalArgumentException("ResourceDto no pot ser null"));
        }

        log.info("Trying to convert {}", resourceDto);

        ResourceDocument resourceDocument = resourceConverter.convertDtoToDocument(resourceDto, ResourceDocument.class);

        if (resourceDocument == null) {
            log.error("Error: resourceConverter is null");
            return Mono.error(new IllegalStateException("Conversion DTO to Document null"));
        }

        if (resourceDocument.getResourceId() == null) {
            resourceDocument.setResourceId(UUID.randomUUID());
        }

        resourceDocument.setContentType(resourceDto.getContentType());
        resourceDocument.setChallengeIds(resourceDto.getChallengeIds());

        return resourceRepository.save(resourceDocument)
                .map(savedResource -> {
                    ResourceDto savedDto = resourceConverter.convertDocumentToDto(savedResource, ResourceDto.class);
                    log.info("Resource created with an ID {}", savedDto.getResourceId());
                    return savedDto;
                })
                .doOnError(error -> log.error("Error occurred when creating resource {}", error.getMessage()));
    }
}