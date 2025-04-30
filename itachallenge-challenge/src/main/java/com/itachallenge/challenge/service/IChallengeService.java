package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.enums.Topic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeById(String id);

    Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage);

    Mono<SolutionDto> addSolution(SolutionDto solutionDto);

    Mono<GenericResultDto<ChallengeDto>> getAllChallenges(int offset, int limit);

    Flux<GenericResultDto<ChallengeDto>> getChallengesByFilter(Optional<String> idLanguage,
                                                               Optional<String> level,
                                                               Optional<List<UUID>> tags,
                                                               int offset,
                                                               int limit);

    Mono<String> updateResourceByUuid(String id, Map<String, Object> updates);

    Mono<ChallengeDto> addChallenge(ChallengeCreateDto challengeCreateDto);

    Mono<DeleteResponseDto> deleteChallengeById(String id);

    Mono<ChallengeListDto> getChallengesByTopic(Topic topic, int page, int size);

    Mono<FavoriteDto> addChallengeToFavorites(String challengeId, String userId);

    Mono<BookmarkDto> addChallengeToBookmarks(String challengeId, String userId);

    Mono<FavoriteDto> removeChallengeFromFavorites(String challengeId, String userId);

    Mono<SolvedDto> addChallengeToSolved(String challengeId, String userId);
  
    Mono<BookmarkDto> removeChallengeFromBookmarks(String challengeId, String userId);

}
