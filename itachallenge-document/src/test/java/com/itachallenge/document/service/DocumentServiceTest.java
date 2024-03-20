package com.itachallenge.document.service;

import com.itachallenge.document.proxy.IAuthClient;
import com.itachallenge.document.proxy.IChallengeClient;
import com.itachallenge.document.proxy.IScoreClient;
import com.itachallenge.document.proxy.IUserClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private IChallengeClient challengeClient;

    @Mock
    private IUserClient userClient;

    @Mock
    private IAuthClient authClient;

    @Mock
    private IScoreClient scoreClient;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void shouldGetSwaggerUserDocsStr() {
        String expectedDocs = "User Swagger Docs";
        Mockito.when(userClient.getSwaggerDocs()).thenReturn(expectedDocs);

        String result = documentService.getSwaggerUserDocsStr();

        assertThat(result).isEqualTo(expectedDocs);
    }

    @Test
    void shouldGetSwaggerChallengeDocsStr() {
        String expectedDocs = "Challenge Swagger Docs";
        Mockito.when(challengeClient.getSwaggerDocs()).thenReturn(expectedDocs);

        String result = documentService.getSwaggerChallengeDocsStr();

        assertThat(result).isEqualTo(expectedDocs);
    }

    @Test
    void shouldGetSwaggerAuthDocsStr() {
        String expectedDocs = "Auth Swagger Docs";
        Mockito.when(authClient.getSwaggerDocs()).thenReturn(expectedDocs);

        String result = documentService.getSwaggerAuthDocsStr();

        assertThat(result).isEqualTo(expectedDocs);
    }

    @Test
    void shouldGetSwaggerScoreDocsStr() {
        String expectedDocs = "Score Swagger Docs";
        Mockito.when(scoreClient.getSwaggerDocs()).thenReturn(expectedDocs);

        String result = documentService.getSwaggerScoreDocsStr();

        assertThat(result).isEqualTo(expectedDocs);
    }
}
