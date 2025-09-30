package com.itachallenge.user.service;

import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class testForFun {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void modifyUserPoints() {
        String uuid = "9d2addc7-3ea0-4a61-851f-bc747dd3fba4";
        StepVerifier.create(userServiceImpl.modifyUserPoints(uuid, 10))
                .expectNext(true)
                .verifyComplete();
    }
}
