package com.itachallenge.challenge.dtos;

import com.itachallenge.challenge.views.ListChallengesConfig;
import com.itachallenge.challenge.views.FilterView;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * note: @ConfigurationProperties don't need @Configuration && this class NO declares @Bean
 */

@Component
@ConfigurationProperties("challenge-section")
class ListChallengesConfigForDtos implements ListChallengesConfig {

    private FilterView technology;
    private FilterView difficulty;
    private FilterView progress;

    private ListChallengesConfigForDtos() {
        //to avoid new instantiations of this class
    }

    //setters for configuration properties binding
    // private/protected setters -> not work
    void setTechnology(FilterDto technology) {
        this.technology = technology;
    }

    void setDifficulty(FilterDto difficulty) {
        this.difficulty = difficulty;
    }

    void setProgress(FilterDto progress) {
        this.progress = progress;
    }

    @Override
    public Set<FilterView> getFilters() {
        return Set.of(technology,difficulty,progress);
    }
}
