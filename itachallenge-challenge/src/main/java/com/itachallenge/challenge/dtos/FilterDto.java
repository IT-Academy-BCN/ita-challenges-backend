package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.views.FilterView;
import lombok.ToString;

import java.util.List;
import java.util.Set;

public class FilterDto implements FilterView {

    @JsonProperty(index = 0)
    private String title;

    @JsonProperty(index = 1)
    private List<String> options;

    @JsonProperty(index = 2)
    private boolean multipleOption;

    @JsonProperty(index = 3)
    private Set<String> visibilities;

    FilterDto(String title, List<String> options, boolean multipleOption, Set<String> visibilities) {
        this.title = title;
        this.options = options;
        this.multipleOption = multipleOption;
        this.visibilities = visibilities;
    }

    private FilterDto() {
        /*
        DO NOT CHANGE VISIBILITY
        MUST BE PRIVATE, or load data from properties on ListChallengesConfigForDtos won't work
         */
    }
/*
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<String> getOptions() {
        return options;
    }

    @Override
    public boolean isMultipleOption() {
        return multipleOption;
    }

    @Override
    public Set<String> getVisibilities() {
        return visibilities;
    }
     */
}
