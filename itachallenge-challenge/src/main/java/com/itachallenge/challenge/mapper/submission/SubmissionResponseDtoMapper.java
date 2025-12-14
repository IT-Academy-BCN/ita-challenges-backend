package com.itachallenge.challenge.mapper.submission;


import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.submission.document.SubmissionDocument;

public final class SubmissionResponseDtoMapper {

    private SubmissionResponseDtoMapper() {
    }

    public static SubmissionResponseDto toDto(SubmissionDocument doc) {
        if (doc == null) {
            return null;
        }

        return SubmissionResponseDto.builder()
                .userId(doc.getUserId().toString())
                .challengeId(doc.getChallengeId().toString())
                .languageId(doc.getLanguageId().toString())
                .submissionText(doc.getSubmissionText())
                .status(doc.getStatus().name())
                .build();
    }
}
