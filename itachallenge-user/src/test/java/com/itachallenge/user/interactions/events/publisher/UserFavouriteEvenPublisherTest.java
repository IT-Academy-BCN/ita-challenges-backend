package com.itachallenge.user.interactions.events.publisher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFavouriteEvenPublisherTest {
    @Test
    void shouldInstantiateUserFavouriteEvenPublisher() {
        UserFavouriteEvenPublisher evenPublisher = new UserFavouriteEvenPublisher();
        assertNotNull(evenPublisher);
    }
}