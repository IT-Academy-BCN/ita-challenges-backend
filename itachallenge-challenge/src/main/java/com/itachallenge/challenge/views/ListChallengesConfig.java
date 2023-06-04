package com.itachallenge.challenge.views;

import java.util.Set;

public interface ListChallengesConfig {

    public static final String TECHNOLOGY_FILTER = "Language Filter";

    public static final String DIFFICULTY_FILTER = "Difficulty Filter";

    public static final String PROGRESS_FILTER = "Progress Filter";

    Set<FilterView> getFilters();
}
