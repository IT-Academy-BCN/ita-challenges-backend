package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;
@Service
public class ResourceService implements IResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private DocumentToDtoConverter<ResourceDocument, ResourceDto> resourceConverter;

    @CacheEvict(value = "resources", allEntries = true)
    @Override
    public Mono<ResourceDto> createResource(ResourceDto resourceDto) {
        ResourceDocument resourceDocument = resourceConverter.convertDocumentToDto(resourceDto, ResourceDocument.class);

        if (resourceDocument.getUuid() == null) {
            resourceDocument.setUuid(UUID.randomUUID());
        }

        return resourceRepository.save(resourceDocument)
                .map(savedResource -> resourceConverter.convertDocumentToDto(savedResource, ResourceDto.class))
                .doOnSuccess(savedResourceDto -> log.info("Resource created with ID: {}", savedResourceDto.getResourceId()))
                .doOnError(error -> log.error("Error occurred while creating resource: {}", error.getMessage()));
    }
}
