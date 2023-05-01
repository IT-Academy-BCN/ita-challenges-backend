package com.itachallenge.challenge.dto.challengessection;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.List;

public class FilterInfoDto {

    private final String filterName;

    private final List<String> options;

    private final boolean uniqueOption;

    private final List<String> visibility;

    private FilterInfoDto(String filterName, List<String> options, boolean uniqueOption, List<String> visibility) {
        this.filterName = filterName;
        this.options = options;
        this.uniqueOption = uniqueOption;
        this.visibility = visibility;
    }

    public static FilterInfoDto forDifficulties(){
        List<String> options = List.of("Fácil", "Media", "Difícil");
        List<String> minRoleWithFilterNotHidden = List.of("ROLE_GUEST");
        return new FilterInfoDto("Dificultad",options,false, minRoleWithFilterNotHidden);
    }

    public static FilterInfoDto forLanguages(){
        List<String> options = List.of("Javascript", "Java", "PHP", "Python");
        List<String> minRoleWithFilterNotHidden = List.of("ROLE_GUEST");
        return new FilterInfoDto("Lenguaje",options,false, minRoleWithFilterNotHidden);
    }

    public static FilterInfoDto forProgress(){
        List<String> options = List.of("No empezados", "Falta completar", "Completados");
        List<String> minRoleWithFilterNotHidden = List.of("ROLE_USER");
        return new FilterInfoDto("Progreso",options,false, minRoleWithFilterNotHidden);
    }

    public int getCount(){
        return options.size();
    }
}
