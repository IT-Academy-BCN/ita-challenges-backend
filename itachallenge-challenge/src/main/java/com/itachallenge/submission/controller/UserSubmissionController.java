package com.itachallenge.submission.controller;



import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import com.itachallenge.submission.service.IUserSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequiredArgsConstructor
//cambiamos el request final?, en lugar de user hacemos submission? o esto cambia el PATH?
@RequestMapping("/itachallenge/api/v1/challenge")
public class UserSubmissionController {

    private static final Logger log = LoggerFactory.getLogger(UserSubmissionController.class);
    private final IUserSubmissionService userSubmissionService;


    @GetMapping("/challenges/{userId}/solutions")
    //si cambio el userSolutionResponseDTO cambio el flujo, ahi viene el mapper?
    //public Mono<ResponseEntity<Flux<UserSolutionResponseDto>>> getAllSolutionsByUser(
    public Mono<ResponseEntity<Flux<UserSubmissionResponseDto>>> getAllSolutionsByUser(
            @PathVariable String userId) {
        return userSubmissionService.getAllSubmissionsByUser(userId)
                .collectList()
                .map(list -> {
                    log.info("Retrieved {} submissions for user {}", list.size(), userId);
                    return ResponseEntity.ok().body(Flux.fromIterable(list));
                });
    }
}

