package com.itachallenge.challenge.dtos;

import com.itachallenge.challenge.views.FilterView;

import java.util.List;
import java.util.Set;

class FilterDto implements FilterView {

    private String title;

    private List<String> options;

    private boolean multipleOption;

    private Set<String> visibilities;

    FilterDto(String title, List<String> options, boolean multipleOption, Set<String> visibilities) {
        this.title = title;
        this.options = options;
        this.multipleOption = multipleOption;
        this.visibilities = visibilities;
    }

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
}
