package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.documents.ChallengeDummy;
import com.itachallenge.challenge.documents.Language;
import com.itachallenge.challenge.documents.LanguageDummy;

public interface StarterConverter {

    ChallengeDocConverter from(Challenge challenge);

    LanguageDocConverter from (Language language);
}