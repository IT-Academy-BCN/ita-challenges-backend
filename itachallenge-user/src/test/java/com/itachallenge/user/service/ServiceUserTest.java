package com.itachallenge.user.service;

import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
public class ServiceUserTest {

    @Mock
    private IUserSolutionRepository userScoreRepository;
    @Mock
    private ConverterDocumentToDto converter;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    } //???


}
