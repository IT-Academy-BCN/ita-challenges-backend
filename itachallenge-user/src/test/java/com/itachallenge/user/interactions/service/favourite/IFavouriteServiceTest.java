package com.itachallenge.user.interactions.service.favourite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IFavouriteServiceTest {
    @Test
    void shouldInstantiateIFavouriteService() {
        IFavouriteService service = new IFavouriteService();
        assertNotNull(service);
    }
}