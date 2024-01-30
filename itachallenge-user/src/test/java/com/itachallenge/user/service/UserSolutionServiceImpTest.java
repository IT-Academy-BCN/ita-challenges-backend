package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.InjectMocks;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserSolutionServiceImpTest {

    @Mock
    IUserSolutionRepository iUserSolutionRepository;
    @InjectMocks
    UserSolutionServiceImp userSolutionServiceImp;

    @Test
    void markAsBookmarked() {
        UUID challengeId = UUID.fromString("b860f3eb-ef9f-43bf-8c3c-9a5318d26a90");
        UUID languageId = UUID.fromString("26cbe8eb-be68-4eb4-96a6-796168e80ec9");
        UUID userId = UUID.fromString("df99bae8-4f7f-4054-a957-37a12aa16364");
        boolean bookmarked = true;
        UserSolutionDocument userSolutionDocument = new UserSolutionDocument();
        userSolutionDocument.setUserId(userId);
        userSolutionDocument.setLanguageId(languageId);
        userSolutionDocument.setChallengeId(challengeId);
        userSolutionDocument.setBookmarked(true);
        when(iUserSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId))
                .thenReturn(Mono.just(userSolutionDocument));

        assertNotNull(userSolutionDocument);
        assert(userSolutionDocument.isBookmarked());
        assert (userSolutionDocument.getUserId().equals(userId));
        assert (userSolutionDocument.getLanguageId().equals(languageId));
        assert (userSolutionDocument.getChallengeId().equals(challengeId));

    }
}