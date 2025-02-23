package com.itachallenge.challenge.service;
import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ResourceRepository;
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

    public ResourceService(ResourceRepository resourceRepository,
                           DocumentToDtoConverter<ResourceDocument, ResourceDto> resourceConverter,
                           IChallengeService challengeService) {
        this.resourceRepository = resourceRepository;
        this.resourceConverter = resourceConverter;
        this.challengeService = challengeService;
    }

    @CacheEvict(value = "resources", allEntries = true)
    @Override
    public Mono<ResourceDto> createResource(ResourceDto resourceDto) {
        return challengeService.getChallengesByTopic(resourceDto.getTopic(), 0, -1)
                .flatMap(challengeResult -> {
                    List<ChallengeDto> matchingChallenges = challengeResult != null && challengeResult.getResults() != null
                            ? Arrays.asList(challengeResult.getResults())
                            : new ArrayList<>();

                    if (matchingChallenges.size() == 1) {
                        resourceDto.setChallengeIds(Collections.singletonList(matchingChallenges.get(0).getChallengeId()));
                    } else if (!matchingChallenges.isEmpty()) {
                        resourceDto.setChallengeIds(matchingChallenges.stream()
                                .map(ChallengeDto::getChallengeId)
                                .collect(Collectors.toList()));
                    }

                    return saveResource(resourceDto);
                })
                .switchIfEmpty(saveResource(resourceDto))
                .onErrorResume(error -> {
                    log.error("Error creating resource: {}", error.getMessage());
                    return Mono.error(new RuntimeException("Error creating resource"));
                });
    }


    private Mono<ResourceDto> saveResource(ResourceDto resourceDto) {
        ResourceDocument resourceDocument = resourceConverter.convertDtoToDocument(resourceDto, ResourceDocument.class);

        if (resourceDocument.getUuid() == null) {
            resourceDocument.setUuid(UUID.randomUUID());
        }

        return resourceRepository.save(resourceDocument)
                .map(savedResource -> resourceConverter.convertDocumentToDto(savedResource, ResourceDto.class))
                .doOnSuccess(savedResourceDto -> log.info("Resource created with ID: {}", savedResourceDto.getResourceId()))
                .doOnError(error -> log.error("Error occurred while creating resource: {}", error.getMessage()));
    }
}
