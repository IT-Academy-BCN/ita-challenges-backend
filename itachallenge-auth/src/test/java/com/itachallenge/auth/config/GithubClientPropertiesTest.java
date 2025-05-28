package com.itachallenge.auth.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class GithubClientPropertiesTest {

    @Test
    void testGetterAndSetterEnvironments() {
        GithubClientProperties properties = new GithubClientProperties();

        Map<String, ClientConfig> map = new HashMap<>();
        ClientConfig config = new ClientConfig();
        config.setClientId("id123");
        map.put("dev", config);

        properties.setEnvironments(map);

        assertThat(properties.getEnvironments()).isEqualTo(map);
    }

    @Test
    void testGetClientConfig_ExistingKey() {
        GithubClientProperties properties = new GithubClientProperties();

        ClientConfig config = new ClientConfig();
        config.setClientId("id123");

        Map<String, ClientConfig> map = new HashMap<>();
        map.put("dev", config);

        properties.setEnvironments(map);

        ClientConfig result = properties.getClientConfig("dev");

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("id123");
    }

    @Test
    void testGetClientConfig_NonExistingKey() {
        GithubClientProperties properties = new GithubClientProperties();

        Map<String, ClientConfig> map = new HashMap<>();
        properties.setEnvironments(map);

        ClientConfig result = properties.getClientConfig("prod");

        assertThat(result).isNull();
    }
}
