package com.itachallenge.challenge.custom.servicesC;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DemoServiceCTest {

    @Autowired
    private DemoServiceC demoService;

    //enable dependency
    @Test
    void saveOneChallengeWithDBRefEnabledTest(){
        demoService.saveOneChallengeWithDBRefEnabled();
    }


    //disable dependency
    @Test
    void saveOneChallengeWithoutNoReactiveDependency(){
        demoService.saveOneChallengeWithoutNoReactiveDependency();
    }
}
