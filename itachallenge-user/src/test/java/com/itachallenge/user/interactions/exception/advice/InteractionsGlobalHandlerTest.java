package com.itachallenge.user.interactions.exception.advice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionsGlobalHandlerTest {
    @Test
    void shouldInstantiateInteractionsGlobalHandler() {
        InteractionsGlobalHandler globalHandler = new InteractionsGlobalHandler();
        assertNotNull(globalHandler);
    }
}