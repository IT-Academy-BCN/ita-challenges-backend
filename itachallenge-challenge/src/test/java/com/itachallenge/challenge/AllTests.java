package com.itachallenge.challenge;

import com.itachallenge.challenge.controller.AllControllersTest;
import com.itachallenge.challenge.helper.AllHelpersTest;
import com.itachallenge.challenge.service.AllServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AllHelpersTest.class,
        AllServiceTests.class,
        AllControllersTest.class
})
public class AllTests {
}
