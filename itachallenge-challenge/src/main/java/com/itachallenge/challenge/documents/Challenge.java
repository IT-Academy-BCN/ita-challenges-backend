package com.itachallenge.challenge.documents;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface Challenge {

    UUID getUuid();

    String getLevel();

    String getTitle();

    Set<Language> getLanguages();

    LocalDateTime getCreationDate();

}
