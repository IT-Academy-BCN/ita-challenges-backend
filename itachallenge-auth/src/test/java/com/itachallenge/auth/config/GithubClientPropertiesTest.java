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

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());

        config1.setClientId("id");
        assertNotEquals(config1, config2);
    }

    @Test
    void environmentsGetterSetterAndGetClientConfigTest() {
        GithubClientProperties props = new GithubClientProperties();
        Map<String, GithubClientProperties.ClientConfig> envs = new HashMap<>();

        GithubClientProperties.ClientConfig devConfig = new GithubClientProperties.ClientConfig();
        devConfig.setClientId("dev-client-id");
        devConfig.setClientSecret("dev-client-secret");
        devConfig.setRedirectUri("https://dev.example.com/callback");

        envs.put("dev", devConfig);
        props.setEnvironments(envs);
        assertNotNull(props.getEnvironments());
        assertEquals(devConfig, props.getClientConfig("dev"));
        assertNull(props.getClientConfig("unknown"));
    }

    @Test
    void clientConfigGetterSetterTest() {
        GithubClientProperties.ClientConfig config = new GithubClientProperties.ClientConfig();

        config.setClientId("client-id");
        config.setClientSecret("client-secret");
        config.setRedirectUri("https://example.com/callback");

        assertEquals("client-id", config.getClientId());
        assertEquals("client-secret", config.getClientSecret());
        assertEquals("https://example.com/callback", config.getRedirectUri());
    }

    @Test
    void clientConfig_EqualsWithNullFields() {
        GithubClientProperties.ClientConfig config1 = new GithubClientProperties.ClientConfig();
        GithubClientProperties.ClientConfig config2 = new GithubClientProperties.ClientConfig();

        config1.setClientId(null);
        config1.setClientSecret("secret");
        config1.setRedirectUri("redirect");

        config2.setClientId("id");
        config2.setClientSecret("secret");
        config2.setRedirectUri("redirect");

        assertNotEquals(config1, config2);

        config1.setClientId("id");
        config2.setClientId(null);
        assertNotEquals(config1, config2);
    }

    @Test
    void clientConfig_HashCodeWithNullFields() {
        GithubClientProperties.ClientConfig config1 = new GithubClientProperties.ClientConfig();
        config1.setClientId(null);
        config1.setClientSecret("secret");
        config1.setRedirectUri("redirect");

        GithubClientProperties.ClientConfig config2 = new GithubClientProperties.ClientConfig();
        config2.setClientId(null);
        config2.setClientSecret("secret");
        config2.setRedirectUri("redirect");

        assertEquals(config1.hashCode(), config2.hashCode());
    }
}