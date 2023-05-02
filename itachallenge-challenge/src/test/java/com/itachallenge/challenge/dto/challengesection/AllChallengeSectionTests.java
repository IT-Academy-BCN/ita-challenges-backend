package com.itachallenge.challenge.dto.challengesection;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


@Suite
@SelectClasses({
        ChallengesSectionInfoDtoTest.class,
        FilterDtoTest.class,
        FilterInfoDtoTest.class,
        SortInfoDtoTest.class
})
public class AllChallengeSectionTests {
}
