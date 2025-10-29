package com.itachallenge.user.interactions.controller.favourite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavouriteControllerTest {
    @Test
    void shouldInstantiateFavouriteController() {
        FavouriteController controller = new FavouriteController();
        assertNotNull(controller);
    }

}