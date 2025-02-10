package com.itachallenge.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteResponseDto {
    private String id;
    private String message;
}
