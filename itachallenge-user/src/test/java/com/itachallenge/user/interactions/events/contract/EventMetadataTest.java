package com.itachallenge.user.interactions.events.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventMetadataTest {
    @Test
    void shouldInstantiateEventMetadata() {
        EventMetadata metadata = new EventMetadata();
        assertNotNull(metadata);
    }
}