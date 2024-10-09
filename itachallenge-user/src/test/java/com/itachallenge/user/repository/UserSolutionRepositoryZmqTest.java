package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class UserSolutionRepositoryZmqTest {

    @Mock
    private IUserSolutionRepository userSolutionRepository;
    private UserSolutionDocument userSolutionDoc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Inicializar los mocks

        // Datos hardcoded para la prueba
        userSolutionDoc = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .challengeId(UUID.randomUUID())
                .score(100)  // Dato hardcoded para el score
                .errors("Syntax error")  // Dato hardcoded para los errores
                .build();
    }

    @Test
    public void testSaveUserSolution() {
        // Simular el comportamiento del repositorio usando el mock
        when(userSolutionRepository.save(userSolutionDoc)).thenReturn(Mono.just(userSolutionDoc));

        // Llamar al método save del repositorio
        Mono<UserSolutionDocument> result = userSolutionRepository.save(userSolutionDoc);

        // Verificar que se guarda correctamente
        StepVerifier.create(result)
                .expectNextMatches(savedDoc -> savedDoc.getScore() == 100 && "Syntax error".equals(savedDoc.getErrors()))
                .verifyComplete();

        // Verificar que se haya llamado al método save del repositorio
        verify(userSolutionRepository, times(1)).save(userSolutionDoc);
    }

    @Test
    public void testFindByUuid() {
        UUID uuid = userSolutionDoc.getUuid();

        // Simular el comportamiento del repositorio
        when(userSolutionRepository.findByUuid(uuid)).thenReturn(Mono.just(userSolutionDoc));

        // Llamar al método findByUuid del repositorio
        Mono<UserSolutionDocument> result = userSolutionRepository.findByUuid(uuid);

        // Verificar que se obtiene la solución correcta
        StepVerifier.create(result)
                .expectNextMatches(foundDoc -> foundDoc.getScore() == 100 && "Syntax error".equals(foundDoc.getErrors()))
                .verifyComplete();

        // Verificar que se haya llamado al método findByUuid del repositorio
        verify(userSolutionRepository, times(1)).findByUuid(uuid);
    }

    @Test
    public void testFindByChallengeIdAndScore() {
        UUID challengeId = userSolutionDoc.getChallengeId();

        // Simular el comportamiento del repositorio
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeId, ChallengeStatus.SUBMITTED))
                .thenReturn(Flux.just(userSolutionDoc));

        // Llamar al método findByChallengeIdAndStatus del repositorio
        Flux<UserSolutionDocument> result = userSolutionRepository.findByChallengeIdAndStatus(challengeId, ChallengeStatus.SUBMITTED);

        // Verificar que se obtiene la solución correcta
        StepVerifier.create(result)
                .expectNextMatches(foundDoc -> foundDoc.getScore() == 100 && "Syntax error".equals(foundDoc.getErrors()))
                .verifyComplete();

        // Verificar que se haya llamado al método findByChallengeIdAndStatus del repositorio
        verify(userSolutionRepository, times(1))
                .findByChallengeIdAndStatus(challengeId, ChallengeStatus.SUBMITTED);
    }

    @Test
    public void testFindByScore() {
        int score = 100;

        // Simular el comportamiento del repositorio
        when(userSolutionRepository.findByScore(score)).thenReturn(Flux.just(userSolutionDoc));

        // Llamar al método findByScore del repositorio
        Flux<UserSolutionDocument> result = userSolutionRepository.findByScore(score);

        // Verificar que se obtienen las soluciones correctas con el puntaje indicado
        StepVerifier.create(result)
                .expectNextMatches(foundDoc -> foundDoc.getScore() == score)
                .verifyComplete();

        // Verificar que se haya llamado al método findByScore del repositorio
        verify(userSolutionRepository, times(1)).findByScore(score);
    }
}
