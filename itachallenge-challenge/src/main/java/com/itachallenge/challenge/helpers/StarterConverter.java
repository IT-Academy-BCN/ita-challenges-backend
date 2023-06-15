package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.documents.Language;

public interface StarterConverter {

    ChallengeDocConverter from(Challenge challenge);

    LanguageDocConverter from (Language language);
}