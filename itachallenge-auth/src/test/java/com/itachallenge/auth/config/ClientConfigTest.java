package com.itachallenge.auth.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class ClientConfigTest {

    @Test
    void lombokGeneratedMethods_workCorrectly() {
        ClientConfig config1 = new ClientConfig();
        config1.setClientId("abc123");
        config1.setClientSecret("secretXYZ");
        config1.setRedirectUri("http://localhost/callback");

        // Getters
        assertThat(config1.getClientId()).isEqualTo("abc123");
        assertThat(config1.getClientSecret()).isEqualTo("secretXYZ");
        assertThat(config1.getRedirectUri()).isEqualTo("http://localhost/callback");

        // toString contains field values
        String toString = config1.toString();
        assertThat(toString).contains("clientId=abc123");
        assertThat(toString).contains("clientSecret=secretXYZ");
        assertThat(toString).contains("redirectUri=http://localhost/callback");

        // equals and hashCode
        ClientConfig config2 = new ClientConfig();
        config2.setClientId("abc123");
        config2.setClientSecret("secretXYZ");
        config2.setRedirectUri("http://localhost/callback");

        // Objects with same data should be equal
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());

        // Change one property and they should not be equal
        config2.setClientId("differentId");
        assertThat(config1).isNotEqualTo(config2);
    }
}
