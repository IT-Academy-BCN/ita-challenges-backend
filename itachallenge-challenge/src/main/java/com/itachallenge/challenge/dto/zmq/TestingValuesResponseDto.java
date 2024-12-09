package com.itachallenge.challenge.dto.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itachallenge.challenge.dto.ChallengeTestingValuesDto;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TestingValuesResponseDto extends ChallengeTestingValuesDto {

}
