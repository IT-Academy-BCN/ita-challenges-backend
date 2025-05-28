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

        assertThat(config1.getClientId()).isEqualTo("abc123");
        assertThat(config1.getClientSecret()).isEqualTo("secretXYZ");
        assertThat(config1.getRedirectUri()).isEqualTo("http://localhost/callback");

        String toString = config1.toString();
        assertThat(toString).contains("clientId=abc123");
        assertThat(toString).contains("clientSecret=secretXYZ");
        assertThat(toString).contains("redirectUri=http://localhost/callback");

        ClientConfig config2 = new ClientConfig();
        config2.setClientId("abc123");
        config2.setClientSecret("secretXYZ");
        config2.setRedirectUri("http://localhost/callback");

        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());

        config2.setClientId("differentId");
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    void equals_withNullAndDifferentType() {
        ClientConfig config = new ClientConfig();
        assertThat(config).isNotEqualTo(null);
        assertThat(config).isNotEqualTo("some string");
    }

    @Test
    void equals_withSameInstance() {
        ClientConfig config = new ClientConfig();
        assertThat(config).isEqualTo(config);
    }

    @Test
    void equals_withDifferentClientSecret() {
        ClientConfig config1 = new ClientConfig();
        config1.setClientId("id");
        config1.setClientSecret("secret1");
        config1.setRedirectUri("uri");

        ClientConfig config2 = new ClientConfig();
        config2.setClientId("id");
        config2.setClientSecret("secret2");
        config2.setRedirectUri("uri");

        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    void hashCode_isConsistent() {
        ClientConfig config = new ClientConfig();
        config.setClientId("id");
        config.setClientSecret("secret");
        config.setRedirectUri("uri");

        int hash1 = config.hashCode();
        int hash2 = config.hashCode();

        assertThat(hash1).isEqualTo(hash2);
    }

}
