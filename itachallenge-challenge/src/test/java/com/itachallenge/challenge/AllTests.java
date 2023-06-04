package com.itachallenge.challenge;

import com.itachallenge.challenge.controller.ChallengeControllerTest;
import com.itachallenge.challenge.dtos.ChallengeBasicInfoDtoTest;
import com.itachallenge.challenge.dtos.ChallengeDtoTest;
import com.itachallenge.challenge.dtos.LanguageDtoTest;
import com.itachallenge.challenge.helpers.CustomConverterTest;
import com.itachallenge.challenge.helpers.ResourceHelperTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ResourceHelperTest.class,

        LanguageDtoTest.class,
        ChallengeBasicInfoDtoTest.class,
        ChallengeDtoTest.class,

        CustomConverterTest.class,

        ChallengeControllerTest.class,
})
public class AllTests {
}