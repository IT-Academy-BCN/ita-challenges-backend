package com.itachallenge.challenge.dtos;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Component
@Getter
@Setter
public class ChallengeDto {

    private UUID id_challenge;
}
