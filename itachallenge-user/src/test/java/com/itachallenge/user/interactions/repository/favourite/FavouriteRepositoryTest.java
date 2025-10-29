package com.itachallenge.user.interactions.repository.favourite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavouriteRepositoryTest {
    @Test
    void shouldInstantiateFavouriteRepository() {
        FavouriteRepository repository = new FavouriteRepository();
        assertNotNull(repository);
    }
}