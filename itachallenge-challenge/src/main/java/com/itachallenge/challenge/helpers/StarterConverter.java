package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.ChallengeI;
import com.itachallenge.challenge.documents.LanguageI;

public interface StarterConverter {

    ChallengeDocConverter from(ChallengeI challengeI);

    LanguageDocConverter from (LanguageI languageI);
}