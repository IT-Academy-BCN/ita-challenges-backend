package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterInfoDto {

    private String filterName;

    private List<String> options;

    private boolean uniqueOption;

    private List<String> visibility;

    private FilterInfoDto() {
        //for tests when deserialization
    }

    private FilterInfoDto(String filterName, List<String> options, boolean uniqueOption, List<String> visibility) {
        this.filterName = filterName;
        this.options = options;
        this.uniqueOption = uniqueOption;
        this.visibility = visibility;
    }

    public static FilterInfoDto forLanguages(){
        List<String> options = List.of("Javascript", "Java", "PHP", "Python");  //TODO: get from enum
        List<String> minRoleWithFilterNotHidden = List.of("ROLE_GUEST");  //TODO: get from enum
        return new FilterInfoDto("Lenguaje",options,false, minRoleWithFilterNotHidden);
    }

    public static FilterInfoDto forDifficulties(){
        List<String> options = List.of("Fácil", "Media", "Difícil");  //TODO: get from enum
        List<String> minRoleWithFilterNotHidden = List.of("ROLE_GUEST");  //TODO: get from enum
        return new FilterInfoDto("Dificultad",options,false, minRoleWithFilterNotHidden);
    }

    public static FilterInfoDto forProgress(){
        List<String> options = List.of("No empezados", "Falta completar", "Completados");  //TODO: get from enum
        List<String> minRoleWithFilterNotHidden = List.of("ROLE_USER");  //TODO: get from enum
        return new FilterInfoDto("Progreso",options,false, minRoleWithFilterNotHidden);
    }

    public int getCount(){
        return options.size();
    }
}
