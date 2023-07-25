package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Validates {

    @Autowired
    private PropertiesConfig propertiesConfig;

    public boolean isValidLevel(String level) {
        return level != null && Stream.of(propertiesConfig.getEasy(), propertiesConfig.getMedium(), propertiesConfig.getHard())
                .filter(description -> description != null) // Filtrar descripciones que no sean nulas
                .anyMatch(description -> description.equalsIgnoreCase(level));
    }


    public boolean isValidLanguage(String language) {
        return language != null && Stream.of(propertiesConfig.getJava(), propertiesConfig.getJavascript(), propertiesConfig.getPhp(), propertiesConfig.getPython())
                .anyMatch(description -> description.equalsIgnoreCase(language));
    }

    public void validLenguageLevel(Set<String> level, Set<String> language){
        Set<String> upperCaseLevel = level.stream().map(String::toUpperCase).collect(Collectors.toSet());
        Set<String> upperCaseLanguage = language.stream()
                .map(s -> s.equalsIgnoreCase("PHP") ? "PHP" : s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.toSet());

        boolean validLevel = upperCaseLevel.stream().allMatch(this::isValidLevel);
        boolean validLanguage = upperCaseLanguage.stream().allMatch(this::isValidLanguage);

        if (validLevel && validLanguage) {
        } else if (!validLevel && !validLanguage) {
            throw new BadRequestException("Invalid level(s) and language(s): " + level + ", " + language);
        } else if (!validLevel) {
            throw new BadRequestException("Invalid level(s): " + level);
        } else {
            throw new BadRequestException("Invalid language(s): " + language);
        }
    }

}
