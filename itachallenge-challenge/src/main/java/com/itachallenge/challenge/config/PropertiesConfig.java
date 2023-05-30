package com.itachallenge.challenge.config;

import com.itachallenge.challenge.helpers.ResourceHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PropertiesConfig {

    @Value("${dummy.filters-info.path}")
    private String filterPath;

    @Value("${dummy.sort-info.path}")
    private String sortPath;

    //TODO: update method when implementing load data from properties instead from dummy
    public String loadFiltersData(){
        try {
            return new ResourceHelper(filterPath).readResourceAsString();
        }catch (IOException ex){
            return ex.getMessage();
        }
    }

    //TODO: update method when implementing load data from properties instead from dummy
    public String loadSortData(){
        try {
            return new ResourceHelper(sortPath).readResourceAsString();
        }catch (IOException ex){
            return ex.getMessage();
        }
    }
}
