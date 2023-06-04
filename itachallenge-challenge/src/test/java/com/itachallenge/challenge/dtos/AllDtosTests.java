package com.itachallenge.challenge.dtos;

import com.itachallenge.challenge.views.FilterView;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import java.util.List;
import java.util.Set;

@Suite
@SelectClasses({
        FilterDtoTest.class,
        ListChallengesConfigForDtosTest.class
})
public class AllDtosTests {

    public static Set<FilterView> buildExpectedFilters(){
        List<String> languagjeOptions = List.of("Javascript","Java","PHP", "Python");
        Set<String> onlyGuestVisibilities = Set.of("ROLE_GUEST");
        FilterView filterLanguage = FilterDtoTest
                .buildFilterDto("Lenguaje", languagjeOptions, true, onlyGuestVisibilities);

        List<String> difficultyOptions = List.of("Fácil","Media","Difícil");
        FilterView filterDifficulty = FilterDtoTest
                .buildFilterDto("Dificultad", difficultyOptions, true, onlyGuestVisibilities);

        List<String> progressOptions = List.of("No empezados","Falta completar","Completados");
        Set<String> onlyUserVisibilities = Set.of("ROLE_USER");
        FilterView filterProgress = FilterDtoTest
                .buildFilterDto("Progreso", progressOptions, true, onlyUserVisibilities);
        return Set.of(filterLanguage, filterDifficulty, filterProgress);
    }
}
