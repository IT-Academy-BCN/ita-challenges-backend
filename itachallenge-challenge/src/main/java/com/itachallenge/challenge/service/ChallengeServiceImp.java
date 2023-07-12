package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImp implements IChallengeService {
    //VARIABLES
    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private Converter converter;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<ChallengeDto> getChallengeId(UUID id) {
        return Mono.from(converter.fromChallengeToChallengeDto(Flux.from(challengeRepository.findByUuid(id))));
    }

    @Override //Comprueba si la UUID es valida.
    public boolean isValidUUID(String id) {
        return !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();
    }


    public boolean removeResourcesByUuid(UUID idResource) {
        Flux<ChallengeDocument> challengeFlux = challengeRepository.findAllByResourcesContaining(idResource);

        return challengeFlux.flatMap(challenge -> {
                    challenge.setResources(challenge.getResources().stream()
                            .filter(s -> !s.equals(idResource))
                            .collect(Collectors.toSet()));
                    return challengeRepository.save(challenge);
                })
                .hasElements()
                .blockOptional()
                .orElse(false);
    }

    public Mono<GenericResultDto<ChallengeDto>> getChallengesPaginated(int pageNumber, int pageSize) {
        Flux<ChallengeDto> challengeDtoFlux = converter.fromChallengeToChallengeDto(reactiveMongoTemplate
                .find(paginationQuery(pageNumber, pageSize), ChallengeDocument.class));

        return challengeDtoFlux.collectList().map(challenges -> {
            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, challenges.size(), challenges.size(), challenges.toArray(new ChallengeDto[0]));
            return resultDto;
        });
    }

    public Query paginationQuery(int pageNumber, int pageSize) {
        return new Query().skip((pageNumber - 1) * pageSize).limit(pageSize);
    }
}
