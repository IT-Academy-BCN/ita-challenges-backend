package com.itachallenge.challenge.documents;

import java.util.Set;
import java.util.UUID;

public interface LanguageI {

    int getIdLanguage();

    String getLanguageName();

    Set<UUID> getIdChallenges();

}
