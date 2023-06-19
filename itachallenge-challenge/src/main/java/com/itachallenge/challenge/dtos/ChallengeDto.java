package com.itachallenge.challenge.dtos;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
@Setter
public class ChallengeDto {

    private UUID id_challenge;
}
