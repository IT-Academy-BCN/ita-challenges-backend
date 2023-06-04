package com.itachallenge.challenge.dtos;

import com.itachallenge.challenge.views.FilterView;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ListChallengesConfigForDtosTest {

    @Autowired
    private ListChallengesConfigForDtos config;

    @Test
    @DisplayName("Test to check if filters data are loaded from properties correctly")
    void getFiltersTest(){
        Set<FilterView> results = config.getFilters();
        //System.out.println(results);
        Set<FilterView> expectedFilters = AllDtosTests.buildExpectedFilters();
        Assertions.assertThat(results).hasSize(expectedFilters.size());
        Assertions.assertThat(results).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedFilters.toArray(FilterView[]::new));
    }
}
