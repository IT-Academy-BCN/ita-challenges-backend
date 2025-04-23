package com.itachallenge.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookmarkDto {

    private boolean isBookmarked;

    private int timesBookmarked;

}