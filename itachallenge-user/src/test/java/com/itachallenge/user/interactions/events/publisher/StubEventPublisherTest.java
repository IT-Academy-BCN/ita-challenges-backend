package com.itachallenge.user.interactions.events.publisher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StubEventPublisherTest {
    @Test
    void shouldInstantiateStubEventPublisher() {
        StubEventPublisher eventPublisher = new StubEventPublisher();
        assertNotNull(eventPublisher);
    }
}