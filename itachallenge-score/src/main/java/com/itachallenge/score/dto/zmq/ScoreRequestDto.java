package com.itachallenge.score.dto.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScoreRequestDto {

    @JsonProperty(value = "id_user", index = 0)
    private UUID userId;

    @JsonProperty(value = "id_challenge", index = 1)
    private UUID challengeId;

    @JsonProperty(value = "score", index = 2)
    private Integer score;

    @JsonProperty("attempts")
    private Integer attempts;

    @JsonProperty("time_taken")
    private Integer timeTaken;

    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("attempts", this.attempts);
        parameters.put("time_taken", this.timeTaken);
        return parameters;
    }

}