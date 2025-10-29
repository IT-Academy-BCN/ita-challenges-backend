package com.itachallenge.user.interactions.events.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFavouriteChangedV1Test {
    @Test
    void shouldInstantiateUserFavouriteChangedV1() {
        UserFavouriteChangedV1 changedV1 = new UserFavouriteChangedV1();
        assertNotNull(changedV1);
    }
}