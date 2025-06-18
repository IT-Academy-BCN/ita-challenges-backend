package com.itachallenge.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SolvedDto {

    private boolean isSolved;

    private int timesSolved;
}
