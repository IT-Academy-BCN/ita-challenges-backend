package com.itachallenge.challenge;
import com.itachallenge.challenge.dtos.AllDtosTests;
import com.itachallenge.challenge.logic.AllLogicTests;
import com.itachallenge.challenge.rest.AllRestTests;
import com.itachallenge.challenge.utils.AllUtilsTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AllUtilsTests.class,
        AllDtosTests.class,
        AllLogicTests.class,
        AllRestTests.class
})
public class AllTests {
}