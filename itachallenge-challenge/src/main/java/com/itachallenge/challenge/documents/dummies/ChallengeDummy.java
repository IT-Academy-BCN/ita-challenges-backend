package com.itachallenge.challenge.documents.dummies;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//@Document(collection="challenges")
public class ChallengeDummy{

    //TODO: UPDATE IFNO
    public ChallengeDummy(UUID uuid, String level, String title, Set<LanguageDummy> languages, LocalDate creationDate, DetailDummy detail, List<SolutionDummy> solutions, Set<UUID> relatedChallenges, Set<UUID> resources) {

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