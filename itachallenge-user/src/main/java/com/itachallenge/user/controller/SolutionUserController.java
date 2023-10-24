package com.itachallenge.user.controller;

import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.service.SolutionUserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/itachallenge/api/v1/user")
public class SolutionUserController {

    @Autowired
    private SolutionUserServiceImpl solutionUserService;

    @Operation(summary = "Create a user solution for a specific challenge in a specific language")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @PostMapping(value = "/solution")
    public ResponseEntity<Mono<SolutionUserDto>> createUserSolution(
            @RequestParam UUID idUser,
            @RequestParam UUID idChallenge,
            @RequestParam UUID idLanguage,
            @RequestBody String solutionText) {
        Mono<SolutionUserDto> createdSolution = solutionUserService.createUserSolution(idUser, idChallenge, idLanguage, solutionText);
        return ResponseEntity.ok(createdSolution);
    }

}
