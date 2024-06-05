package com.itachallenge.user.dtos.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter

public class TestValuesResponseDto {

    @JsonProperty(value="in_param", index = 0)
    private UUID challengeId;

    @JsonProperty(value="out_param", index = 1)
    private UUID languageId;
}
