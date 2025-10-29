package com.itachallenge.user.interactions.exception.favourite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavouriteExceptionTest {
    @Test
    void shouldInstantiateFavouriteException() {
        FavouriteException exception = new FavouriteException();
        assertNotNull(exception);
    }
}