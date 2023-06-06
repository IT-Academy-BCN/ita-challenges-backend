package com.itachallenge.challenge.documents.dummies;

import com.itachallenge.challenge.documents.team.LanguageTeam;

import java.util.Set;
import java.util.UUID;

//@Document(collection="challenges")
public class LanguageDummy implements LanguageTeam {

    public int getIdLanguage() {
        return 0;
    }

    public String getLanguageName() {
        return null;
    }

    public Set<UUID> getIdChallenges() {
        return null;
    }
}
