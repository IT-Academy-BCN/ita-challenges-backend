package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.ChallengeDummy;
import com.itachallenge.challenge.documents.LanguageDummy;

public interface StarterConverter {

    ChallengeDocConverter from(ChallengeDummy challenge);

    LanguageDocConverter from (LanguageDummy language);
}