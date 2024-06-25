package com.itachallenge.score.dto.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScoreResponseDto {

    @JsonProperty(value="percent", index = 0)
    private Integer percent;

    /* ??
    @JsonProperty(value="bookmarks", index = 1)
    private Integer bookmarks;
    */
}
