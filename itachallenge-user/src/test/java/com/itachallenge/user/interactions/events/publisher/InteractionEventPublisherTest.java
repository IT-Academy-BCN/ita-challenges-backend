package com.itachallenge.user.interactions.events.publisher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionEventPublisherTest {
    @Test
    void shouldInstantiateInteractionEventPublisher() {
        InteractionEventPublisher eventPublisher = new InteractionEventPublisher();
        assertNotNull(eventPublisher);
    }
}