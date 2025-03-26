package com.itachallenge.challenge.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FavoriteDto {

    private boolean isFavorite;

    private int timesFavorited;

}
