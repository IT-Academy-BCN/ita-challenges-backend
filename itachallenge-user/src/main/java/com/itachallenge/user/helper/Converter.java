package com.itachallenge.user.helper;

import com.itachallenge.user.document.SolutionUser;
import com.itachallenge.user.dtos.SolutionUserDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class Converter {

    public Mono<SolutionUserDto> fromSolutionUserToSolutionUserDto(Mono<SolutionUser> solutionUserMono) {
        return solutionUserMono.flatMap(solutionUser -> {
            SolutionUserDto solutionUserDto = toSolutionUserDto(solutionUser);
            return Mono.just(solutionUserDto);
        });
    }

    public SolutionUserDto toSolutionUserDto(SolutionUser solutionUser) {
        return SolutionUserDto.builder()
                    .uuid_SolutionUser(solutionUser.getUuid_solutionUser())
                    .uuid_user(solutionUser.getUuid_user())
                    .uuid_challenge(solutionUser.getUuid_challenge())
                    .uuid_language(solutionUser.getUuid_language())
                    .solution_Text(solutionUser.getSolution_Text())
                    .build();
    }


}
