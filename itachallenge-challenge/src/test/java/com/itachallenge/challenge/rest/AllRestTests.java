package com.itachallenge.challenge.rest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ChallengeControllerTest.class
})
public class AllRestTests {
}
