package com.itachallenge.challenge.documents;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChallengeI {


    UUID getId_challenge(); //@MongoId

    //Basic info:
    String getLevel(); //valor seteado fom properties
    String getChallenge_title();
    Set<LanguageI> getLanguages(); //@DBRef

    /*
    Esquema de las fechas:
    Tanto en BD, como en documents, como en dtos:
    Formato YYYY-MM-DD
    En dto, al serializar, json resultante debe tener el formato "20 Dec 2023" (igual a figma)
    TODO: indicar a jonatan que formato de fecha en data-challenge.json (ya en develop)
     se debería modificar el formato
     */
    LocalDate getCreation_date(); //LocalDate (package time) mejor que Date (package utils). Java recomendation



    //NO BASIC INFO

    //for details section -> pesa poco -> embeber y retornar con el challenge
    //así Front end lo puede mostrar cuando usuario seleccione un challenge
    DetailI getDetail();

    ///////////////////////////////////////////
    //NO ES IMPRESCINDIBLE incluir las siguientes relaciones en challenge
    // Al realizar la peticion de un (o N) challenge solo es necesaria basic info + details (para optimizar)
    // -> se podría eliminar en data-challenge.json

    //Challenge 1 : N Solution
    //for solutions section -> data NO se muestra a "qualquier user"
    //guest -> hidden
    //registered sin haber enviado ninguna solución -> hidden
    //registered con "alguna" solución enviada -> mostrar UNICAMENTE SOLUCIONES DEL LENGUAGE INTENTADO
    //admin or mentor -> mostrar siempre?

    //-> endpoint de la seccion:
    // GET /challenges/{id}/solutions + con seguridad

    // TODO: propuesta jonatan. En lugar que 1 challenge esté associado a N solucions
    //  -> establecer que 1 Solución (que está ya asociada a 1 lenguage) también esté asociada a 1 challenge
    //  -> mucho más práctico: único método necesario en repository:
    //  findSolutionsByChallengeIdAndLanguageIdIn
    //  en lugar de findChallengeSolutionsById + filtrar solutions segun lenguage.
    //  Además se evita hacer N joins x challenge (para cargar data que no se va a mostrar)
    //  en cualquier petición de página de challenges
    List<SolutionI> getSolutions(); //@DBRef

    //enpoint de la seccion:
    // GET /challenges/{id}/relateds

    //opcion A) implica findChallengeById + geRelatedsIds + findChallengeBy(Iterable ids)
    //problema: create challenge && update relates trabajando con el UUID en lugar de la entidad
    Set<UUID> getRelateds();

    //opcion B) implica que al leer un challenge cargarlo con una list de challenges, cada uno de ellos
    //cargado con su list de challenges, cada uno de ellos con su list de challenges.... y así hasta vete
    //tu a saber donde.... Pues imaginate ahora que haya que mostrar la lista -> findAll -> lo anterior * N
    //Set<ChallengeI> getRelateds(); //@DBRef

    //Opción C)
    // Eliminar la relación en Challenge
    //Inventarnos la coleccion ChallengeRelations @Document {
    //
    //      private UUID parentChallengeId;
    //
    //      @DBRef
    //      private Set<Challenge> relateds;
    // }
    // crear challenge -> guardar challenge parent + guardar nuevo ChallengeRelations
    // (con la coleccion de challenges ya existentes/persistidos)
    // update relations -> findByParentChallengeId + getRelates add/remove (Challenge) + save
    // obtener relateds -> findByParentChallengeId + getRelates


    //enpoint de la seccion:
    // GET /challenges/{id}/resources
    //Se puede dejar en Challenge (array de UUID), xo nunca se proporciona la data al Front End
    //Con estos ids se realizaran peticiones de resources a ITA-WIKI
    //Añadir y quitar es problema de ITA-WIKI.
    Set<UUID> getTags();
}
