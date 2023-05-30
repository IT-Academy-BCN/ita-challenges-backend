package com.itachallenge.challenge.documents;

import java.util.Set;
import java.util.UUID;

public interface LanguageI {

    int getId_language(); // @MongoId
    String getLanguage_name();

    //TODO: preguntar si se puede deshabilitar. No hay ningun requerimiento del cliente
    // que implique realizar algo similar findLanguagesBYChallenge_ID OR findAllLanguageChallenges
    // y si fueda necesario es más fácil hacer la petición a la colección de challenges
    Set<UUID> getId_Challenges();

    //Set<ChallengeI> getChallenge(); //@DBRef
}
