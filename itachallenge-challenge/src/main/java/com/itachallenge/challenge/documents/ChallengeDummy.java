package com.itachallenge.challenge.documents;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

//@Document(collection="challenges")
public class ChallengeDummy{

    //TODO: include inside constructor LocalDateTime.now();

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