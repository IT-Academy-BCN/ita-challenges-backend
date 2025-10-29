package com.itachallenge.user.interactions.dto.favourite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavouriteDtoTest {
    @Test
    void shouldInstantiateFavouriteDto() {
        FavouriteDto dto = new FavouriteDto();
        assertNotNull(dto);
    }
}