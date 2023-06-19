package com.itachallenge.challenge.documents;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ChallengeI {

    UUID getUuid();

    String getLevel();

    String getTitle();

    Set<LanguageI> getLanguages();

    LocalDateTime getCreationDate();

}
