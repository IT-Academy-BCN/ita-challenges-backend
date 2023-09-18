package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.validator.UrlParamsValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private final PropertiesConfig config;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    IChallengeService challengeService;

    public ChallengeController(PropertiesConfig config) {
        this.config = config;
    }

    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

        Optional<String> challengeService = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map(s -> s.toString());

        Optional<String> userService = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map(s -> s.toString());

        Optional<String> scoreService = discoveryClient.getInstances("itachallenge-score")
                .stream()
                .findAny()
                .map(s -> s.toString());

        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        log.info("Scanning micros:");
        log.info((userService.isPresent() ? userService.get().toString() : "No Services")
                .concat(System.lineSeparator())
                .concat(challengeService.isPresent() ? challengeService.get().toString() : "No Services")
                .concat(System.lineSeparator())
                .concat(scoreService.isPresent() ? scoreService.get().toString() : "No Services"));

        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        return "Hello from ITA Challenge!!!";
    }

    @GetMapping(path = "/challenges/{challengeId}")
    @Operation(
            operationId = "Get the information from a chosen challenge.",
            summary = "Get to see the Challenge level, its details and the available languages.",
            description = "Sending the ID Challenge through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<GenericResultDto<ChallengeDto>> getOneChallenge(@PathVariable("challengeId") String id) {
        return challengeService.getChallengeById(id);
    }

    @DeleteMapping("/resources/{idResource}")
    @Operation(
            operationId = "Get the information from a chosen resource.",
            summary = "Get to see the resource and all its related parameters.",
            description = "Sending the ID Resource through the URI to retrieve it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "The Resource with given Id was not found.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<GenericResultDto<String>> removeResourcesById(@PathVariable String idResource) {
        return challengeService.removeResourcesByUuid(idResource);
    }

    @GetMapping("/challenges")
    @Operation(
            operationId = "Get all the stored challenges into the Database.",
            summary = "Get to see all challenges and their levels, details and their available languages.",
            description = "Requesting all the challenges through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GenericResultDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "No challenges were found.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges() {
        return challengeService.getAllChallenges();
    }


    /**
     * JVR - La PR no es válida. Es necesario revisar:
     * - La paginación ha de añadirse al método de arriba. No tiene sentido tener dos métodos que hacen lo mismo (el de abajo)
     * - Los parámetros de entrada han de llamarse 'page' y 'size'
     * - ChallengeServiceImp: La invocación al Repository desde ChallengeServiceImp line 101 se efectúa con pageNumber - 1. Parece que debe ser pageNumber (sin restar nada)
     * - ChallengeRepository: el método findAllBy debe llamarse findAll(Pageable pageable)
     * - ChallengeRepository: analizar si es posible extender de ReactiveSortingRepository en lugar de ReactiveMongoRepository (provee funcionalidades de ordenado que 7
     * podemos necesitar después)
     * - Converter: la clase Converter debe renombrarse a 'DocumentToDtoConverter' o similar.
     * No es un Converter genérico, sino que es específico para convertir de Document a Dto
     * - PageParametersDto debe renombrarse a UrlParamsValidator o similar. No es un DTO, sino un validador de parámetros de entrada
     * <p>
     * Referencias:
     * https://thepracticaldeveloper.com/full-reactive-stack-2-backend-webflux/ (muestra diferencias Reactive-NonReactive repository)
     * https://medium.com/@davidpetro/spring-webflux-and-pageable-be55104c234f
     */


    @GetMapping("/challengesByPage")
    @Operation(
            operationId = "Get only the challenges on a page.",
            summary = "Get to see challenges on a page and their levels, details and their available languages.",
            description = "Requesting the challenges for a page sending page number and the number of items per page through the URI from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChallengeDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "No challenges were found.", content = {@Content(schema = @Schema())})
            })
    public Flux<ChallengeDto> getChallengesByPage(@Valid UrlParamsValidator urlParamsValidator,
                                                  @RequestParam(name = "page", required = false, defaultValue = ("${validator.page}")) String page,
                                                  @RequestParam(name = "size", required = false, defaultValue = ("${validator.size}")) String size) {
        return challengeService.getChallengesByPage(Integer.parseInt(page), Integer.parseInt(size));
    }

    @GetMapping("/language")
    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        return challengeService.getAllLanguages();
    }

}
