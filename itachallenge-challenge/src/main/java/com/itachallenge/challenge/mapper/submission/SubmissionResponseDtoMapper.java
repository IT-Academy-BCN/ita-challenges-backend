package com.itachallenge.challenge.mapper.submission;

import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.submission.document.SubmissionDocument;

import java.util.Objects;

public final class SubmissionResponseDtoMapper {

    private SubmissionResponseDtoMapper() {
    }

    public static SubmissionResponseDto toDto(SubmissionDocument doc) {
        Objects.requireNonNull(doc, "SubmissionDocument cannot be null");

        return SubmissionResponseDto.builder()
                .userId(doc.getUserId().toString())
                .challengeId(doc.getChallengeId().toString())
                .languageId(doc.getLanguageId().toString())
                .status(doc.getStatus().name())
                .submissionText(doc.getSubmissionText())
                .build();
    }
}
