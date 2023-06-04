package com.itachallenge.challenge.documents.dummies;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

//@Document(collection="challenges")
public class ChallengeDummy{

    //TODO: UPDATE IFNO (public all args constructor)
    public ChallengeDummy(UUID uuid, String level, String title, Set<LanguageDummy> languages, LocalDate creationDate) {

    }

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

    public LocalDate getCreationDate() {
        return null;
    }
}