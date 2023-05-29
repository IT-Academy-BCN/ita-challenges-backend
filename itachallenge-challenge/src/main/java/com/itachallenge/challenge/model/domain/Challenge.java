package com.itachallenge.challenge.model.domain;


import lombok.Data;

import java.util.Date;
import java.util.List;

public class Challenge {
    private String id;

    private String id_challenge;
    private String challenge_title;
    private String level;
    private Date creation_date;
    private int popularity;
    private Details details;
    private List<Language> languages;
    private List<Solution> solutions;
    private List<Resource> resources;
    private List<String> related;

    // Constructor, getters, and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_challenge() {
        return id_challenge;
    }

    public void setId_challenge(String id_challenge) {
        this.id_challenge = id_challenge;
    }

    public String getChallenge_title() {
        return challenge_title;
    }

    public void setChallenge_title(String challenge_title) {
        this.challenge_title = challenge_title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<String> getRelated() {
        return related;
    }

    public void setRelated(List<String> related) {
        this.related = related;
    }

    @Data
    public static class Details {
        private String description;
        private List<Example> examples;
        private String notes;

        // Constructor, getters, and setters

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Example> getExamples() {
            return examples;
        }

        public void setExamples(List<Example> examples) {
            this.examples = examples;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    @Data
    public static class Example {
        private String id_example;
        private String example_text;

        // Constructor, getters, and setters

        public String getId_example() {
            return id_example;
        }

        public void setId_example(String id_example) {
            this.id_example = id_example;
        }

        public String getExample_text() {
            return example_text;
        }

        public void setExample_text(String example_text) {
            this.example_text = example_text;
        }
    }

    @Data
    public static class Language {
        private String id_language;
        private String language_name;
    }

}

// Constructor,

