package com.itachallenge.user.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BookmarkRequestDto {
    private String uuidChallenge;
    private String uuidLanguage    private String uuidUser;
    private boolean bookmarked;
}
