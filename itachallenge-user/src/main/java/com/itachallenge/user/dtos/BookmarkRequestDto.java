package com.itachallenge.user.dtos;

import com.itachallenge.user.annotations.GenericUUIDValid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

//Se utiliza para devolver un JSON en formato correcto
//en acción de guardar unos challenges como favoritos
//UserSolutionDocument devuelva demasiado atributos.

public class BookmarkRequestDto {

    @GenericUUIDValid(message = "Invalid UUID")
    private String uuid_challenge;

    @GenericUUIDValid(message = "Invalid UUID")
    private String uuid_language;

    @GenericUUIDValid(message = "Invalid UUID")
    private String uuid_user;

    private boolean bookmarked;
}
