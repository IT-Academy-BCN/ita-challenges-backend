package com.itachallenge.challenge.integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ChallengeIT.class
})
public class AllIntegrationsTest {
}
