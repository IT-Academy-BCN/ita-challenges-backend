package com.itachallenge.score.controller;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);


    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Score!!!";
    }
    @PostMapping(value = "/score")
    public Mono<ResponseEntity<ScoreResponse>> createScore(@RequestBody ScoreRequest scoreRequest) {
        return Mono.just(scoreRequest)
                .map(req -> {
                    ScoreResponse scoreResponse = new ScoreResponse();
                    scoreResponse.setUuidChallenge(req.getUuidcChallenge());
                    scoreResponse.setUuidLanguage(req.getUuidLanguage());
                    scoreResponse.setSolutionText(req.getSolutionText());

                    // Lógica de ejemplo para calcular el score
                    int score = calculateScore(req.getSolutionText());
                    scoreResponse.setScore(score);

                    return ResponseEntity.ok(scoreResponse);
                });
    }
    private int calculateScore(String solutionText) {
        // Lógica de ejemplo: basada en la longitud del texto de la solución
        // Esta es una lógica simplificada y debería ser reemplazada por tu lógica real
        return Math.min(99, solutionText.length()); // Asegura que el score no exceda 99
    }

}


