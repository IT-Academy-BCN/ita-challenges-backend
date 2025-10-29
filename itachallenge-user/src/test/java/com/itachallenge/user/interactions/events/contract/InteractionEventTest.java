package com.itachallenge.user.interactions.events.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionEventTest {
    @Test
    void shouldInstantiateInteractionEvent() {
        InteractionEvent event = new InteractionEvent();
        assertNotNull(event);
    }
}