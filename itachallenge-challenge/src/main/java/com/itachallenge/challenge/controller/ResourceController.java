package com.itachallenge.challenge.controller;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.service.IResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/resource")
public class ResourceController {

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);
    private final IResourceService resourceService;

    public ResourceController(IResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(value = "/new")
    @Operation(
            operationId = "Create a new resource",
            summary = "Creates a new resource and associates it with a challenge based on its topic.",
            description = "If a challenge with the same topic exists, it is automatically assigned. If multiple challenges exist, it can be linked to all or remain unassigned.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ResourceDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    public Mono<ResponseEntity<ResourceDto>> createNewResource(@RequestBody @Valid ResourceDto resourceDto) {
        log.info("Creating a new resource {}", resourceDto);
        return resourceService.createResource(resourceDto)
                .map(createdResource -> ResponseEntity.ok().body(createdResource));
    }


    @GetMapping("/challenge/{challengeId}")
    @Operation(summary = "Get resources by challenge ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources found"),
            @ApiResponse(responseCode = "400", description = "Invalid challenge ID"),
            @ApiResponse(responseCode = "404", description = "No resources found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Flux<ResourceDto> getResourcesByChallengeId(@PathVariable UUID challengeId) {
        return resourceService.getResourcesByChallengeId(challengeId);
    }
}

