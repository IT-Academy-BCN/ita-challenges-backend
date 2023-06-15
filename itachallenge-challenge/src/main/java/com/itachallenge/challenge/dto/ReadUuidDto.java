package com.itachallenge.challenge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ReadUuidDto {

    private UUID uuid;
}
