package com.itachallenge.challenge.dtos;

import com.itachallenge.challenge.views.ListChallengesConfig;
import com.itachallenge.challenge.views.FilterView;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * note: @ConfigurationProperties don't need @Configuration && this class NO declares @Bean
 */

@Component
@ConfigurationProperties("challenge-section")
class ListChallengesConfigForDtos implements ListChallengesConfig {
    private Map<String, FilterView> filters;

    private ListChallengesConfigForDtos() {
        //to avoid new instantiations of this class
        filters = new HashMap<>();
    }

    //setters for configuration properties binding
    // private/protected setters -> not work
    void setTechnologyFilter(FilterDto technologyFilter) {
        filters.put(TECHNOLOGY_FILTER, technologyFilter);
    }

    void setDifficultyFilter(FilterDto difficultyFilter) {
        filters.put(DIFFICULTY_FILTER, difficultyFilter);
    }

    void setProgressFilter(FilterDto progressFilter) {
        filters.put(PROGRESS_FILTER, progressFilter);
    }

    @Override
    public Set<FilterView> getFilters() {
        return Set.copyOf(filters.values());
    }
}
