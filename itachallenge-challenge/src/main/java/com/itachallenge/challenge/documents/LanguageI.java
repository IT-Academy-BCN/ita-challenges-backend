package com.itachallenge.challenge.documents;

import java.util.Set;
import java.util.UUID;

public interface LanguageI {

    /**
     * TODO: both ChallengeI and LanguageI are interfaces
     * meant to be either deleted or implemented to the documents
     * once the documents jump into develop
     *
     */

    int getIdLanguage();

    String getLanguageName();

    Set<UUID> getIdChallenges();

}
