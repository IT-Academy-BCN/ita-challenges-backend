package com.itachallenge.auth.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class GithubClientPropertiesTest {

    @Test
    void environmentsGetterSetterTest() {
        GithubClientProperties props = new GithubClientProperties();

        Map<String, GithubClientProperties.ClientConfig> envs = new HashMap<>();

        GithubClientProperties.ClientConfig config = new GithubClientProperties.ClientConfig();
        config.setClientId("id");
        config.setClientSecret("secret");
        config.setRedirectUri("redirect");

        envs.put("test", config);

        props.setEnvironments(envs);

        assertNotNull(props.getEnvironments());
        assertEquals(config, props.getClientConfig("test"));
        assertNull(props.getClientConfig("unknown"));
    }

    @Test
    void clientConfig_GettersSetters_EqualsHashCode() {
        GithubClientProperties.ClientConfig config1 = new GithubClientProperties.ClientConfig();
        config1.setClientId("id1");
        config1.setClientSecret("secret1");
        config1.setRedirectUri("redirect1");

        GithubClientProperties.ClientConfig config2 = new GithubClientProperties.ClientConfig();
        config2.setClientId("id1");
        config2.setClientSecret("secret1");
        config2.setRedirectUri("redirect1");
        GithubClientProperties.ClientConfig config3 = new GithubClientProperties.ClientConfig();
        config3.setClientId("id2");
        config3.setClientSecret("secret2");
        config3.setRedirectUri("redirect2");

        assertEquals("id1", config1.getClientId());
        assertEquals("secret1", config1.getClientSecret());
        assertEquals("redirect1", config1.getRedirectUri());

        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertNotEquals(config1, null);
        assertNotEquals(config1, new Object());

        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    void clientConfig_ToString_NotEmpty() {
        GithubClientProperties.ClientConfig config = new GithubClientProperties.ClientConfig();
        config.setClientId("id");
        config.setClientSecret("secret");
        config.setRedirectUri("redirect");

        String str = config.toString();
        assertTrue(str.contains("id"));
        assertTrue(str.contains("secret"));
        assertTrue(str.contains("redirect"));
    }

    @Test
    void clientConfig_EqualsAndHashCode_NullFields() {
        GithubClientProperties.ClientConfig config1 = new GithubClientProperties.ClientConfig();
        GithubClientProperties.ClientConfig config2 = new GithubClientProperties.ClientConfig();

        // Both empty (all nulls) → should be equal
        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());

        config1.setClientId("id");
        // One has field set, the other null → not equal
        assertNotEquals(config1, config2);
    }

}