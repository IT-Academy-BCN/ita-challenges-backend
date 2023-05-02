package com.itachallenge.challenge;

import com.itachallenge.challenge.controller.AllControllerTest;
import com.itachallenge.challenge.service.AllServiceTests;
import com.itachallenge.challenge.dto.challengesection.AllChallengeSectionTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AllChallengeSectionTests.class,
        AllServiceTests.class,
        AllControllerTest.class
})
public class AllTests {
}
