package com.itachallenge.challenge;

import com.itachallenge.challenge.config.PropertiesConfigTest;
import com.itachallenge.challenge.controller.ChallengeControllerTest;
import com.itachallenge.challenge.helpers.ResourceHelperTest;
import com.itachallenge.challenge.services.ChallengeServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ResourceHelperTest.class,
        PropertiesConfigTest.class,
        ChallengeServiceTest.class,
        ChallengeControllerTest.class,
})
public class AllTests {
}
