package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.dummies.ChallengeDummy;
import com.itachallenge.challenge.documents.dummies.LanguageDummy;

public interface StarterConverter {

    ChallengeDocConverter from(ChallengeDummy challenge);

    LanguageDocConverter from (LanguageDummy language);
}