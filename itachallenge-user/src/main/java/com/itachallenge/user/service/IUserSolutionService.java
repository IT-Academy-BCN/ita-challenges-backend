package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface IUserSolutionService {
    Mono<UserSolutionDocument> markAsBookmarked(String uuidChallenge, String uuidLanguage, String uuidUser, boolean bookmarked);
    Flux<UserSolutionDto> showAllUserSolutions(UUID userUuid);
    Mono<List<ChallengeStatisticsDto>> getChallengeStatistics(List<UUID> challengeIds);
    Mono<Long> getBookmarkCountByIdChallenge(UUID idChallenge);
    Mono<Float> getChallengeUsersPercentage(UUID idChallenge);

}
