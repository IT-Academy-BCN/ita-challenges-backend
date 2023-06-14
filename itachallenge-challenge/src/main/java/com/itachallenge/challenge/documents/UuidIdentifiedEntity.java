package com.itachallenge.challenge.documents;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public abstract class UuidIdentifiedEntity {

    @Id
    protected UUID id;

    public void setId(UUID id) {

        if (this.id != null) {
            throw new UnsupportedOperationException("ID is already defined");
        }

        this.id = id;
    }
}
