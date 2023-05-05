package com.itachallenge.challenge;

import com.itachallenge.challenge.controller.ChallengeControllerTest;
import com.itachallenge.challenge.helper.ResourceHelperTest;
import com.itachallenge.challenge.integration.ChallengeIT;
import com.itachallenge.challenge.service.ChallengeServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ResourceHelperTest.class,
        ChallengeServiceTest.class,
        ChallengeControllerTest.class,
        ChallengeIT.class
})
public class AllTests {
}
