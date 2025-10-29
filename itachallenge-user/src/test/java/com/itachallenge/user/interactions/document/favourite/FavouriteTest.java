package com.itachallenge.user.interactions.document.favourite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavouriteTest {
    @Test
    void shouldInstantiateFavourite() {
        Favourite model = new Favourite();
        assertNotNull(model);
    }
}