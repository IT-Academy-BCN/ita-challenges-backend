package com.itachallenge.challenge.views;

import java.util.Set;

public interface ListChallengesConfig {

    String TECHNOLOGY_FILTER = "Language Filter";

    String DIFFICULTY_FILTER = "Difficulty Filter";

    String PROGRESS_FILTER = "Progress Filter";

    Set<FilterView> getFilters();
}
