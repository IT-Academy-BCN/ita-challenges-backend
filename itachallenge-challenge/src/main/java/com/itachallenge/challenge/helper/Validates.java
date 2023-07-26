package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.config.PropertiesConfig;
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

        isValidLevel(String.valueOf(upperCaseLevel));
        isValidLanguage(String.valueOf(upperCaseLanguage));
    }

}
