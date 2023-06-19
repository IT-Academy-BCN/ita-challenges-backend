package com.itachallenge.challenge.documents;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ChallengeI {

    /**
     * TODO: both ChallengeI and LanguageI are interfaces
     * meant to be either deleted or implemented to the documents
     * once the documents jump into develop
     *
     */

    UUID getUuid();

    String getLevel();

    String getTitle();

    Set<LanguageI> getLanguages();

    LocalDateTime getCreationDate();

}
