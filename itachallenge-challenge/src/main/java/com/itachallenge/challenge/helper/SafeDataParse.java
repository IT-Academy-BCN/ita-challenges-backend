package com.itachallenge.challenge.helper;

import java.util.Optional;
import java.util.UUID;

//Clase para para verificar si un valor entra en peticion como nulo que lo deje en Optional.empty
public class SafeDataParse {

    public static Optional<UUID> safeParseUUID(String raw) {
        try {
            if (raw == null || raw.isBlank() || "null".equalsIgnoreCase(raw)) return Optional.empty();
            return Optional.of(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
