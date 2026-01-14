package com.itachallenge.submission.mapper;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.submission.document.SubmissionDocument;

import java.util.Objects;

public final class SubmissionMapper {

    private SubmissionMapper() {
    }

    public static SubmissionDto toDto(SubmissionDocument doc) {
        Objects.requireNonNull(doc, "SubmissionDocument cannot be null");

        return SubmissionDto.builder()
                .userId(doc.getUserId().toString())
                .challengeId(doc.getChallengeId().toString())
                .languageId(doc.getLanguageId().toString())
                .status(doc.getStatus().name())
                .submissionText(doc.getSubmissionText())
                .build();
    }
}
