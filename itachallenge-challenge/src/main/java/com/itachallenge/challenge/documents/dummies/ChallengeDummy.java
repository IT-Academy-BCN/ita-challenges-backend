package com.itachallenge.challenge.documents.dummies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

//@Document(collection="challenges")
public class ChallengeDummy{

    //TODO: can't use All Args Constructor because creationDate needs to initialize
    // LocalDateTime.now();
    // inside its constructor.

    public UUID getUuid() { //TODO: UPDATE IFNO
        return null;
    }

    public String getLevel() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public Set<LanguageDummy> getLanguages() {
        return null;
    }

    public LocalDateTime getCreationDate() {
        return null;
    }
}