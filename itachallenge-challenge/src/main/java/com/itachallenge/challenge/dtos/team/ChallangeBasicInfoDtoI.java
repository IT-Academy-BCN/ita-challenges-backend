package com.itachallenge.challenge.dtos.team;

import com.itachallenge.challenge.dtos.LanguageDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public interface ChallangeBasicInfoDtoI {

     /*
    all args constructor privado -> instanciar según builder
     */

    /*static ChallengeBasicInfoDtoBuilder builder(){
        return null; // para que compile
    }*/

    String getLevel();

    String getTitle();

    Set<LanguageDto> getLanguages();

    Float getPercentage();

    Integer getPopularity();

    //TODO: modificar la clase devuelta
    LocalDateTime getCreationDate();

}
