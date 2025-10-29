package com.itachallenge.user.interactions.events.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserBookmarkedToggledV1Test {
    @Test
    void shouldInstantiateUserBookmarkedToggledV1() {
        UserBookmarkedToggledV1 toggledV1 = new UserBookmarkedToggledV1();
        assertNotNull(toggledV1);
    }
}