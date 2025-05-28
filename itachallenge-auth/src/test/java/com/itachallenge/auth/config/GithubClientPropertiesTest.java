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

    @Test
    void testEquals_Symmetric() {
        GithubClientProperties props1 = new GithubClientProperties();
        GithubClientProperties props2 = new GithubClientProperties();

        Map<String, ClientConfig> map1 = new HashMap<>();
        ClientConfig config1 = new ClientConfig();
        config1.setClientId("id123");
        map1.put("dev", config1);

        Map<String, ClientConfig> map2 = new HashMap<>();
        ClientConfig config2 = new ClientConfig();
        config2.setClientId("id123");
        map2.put("dev", config2);

        props1.setEnvironments(map1);
        props2.setEnvironments(map2);

        assertThat(props1).isEqualTo(props2);
        assertThat(props2).isEqualTo(props1);
    }

    @Test
    void testEquals_NullAndDifferentType() {
        GithubClientProperties props = new GithubClientProperties();

        assertThat(props).isNotEqualTo(null);
        assertThat(props).isNotEqualTo("some string");
    }

    @Test
    void testEquals_SameInstance() {
        GithubClientProperties props = new GithubClientProperties();
        assertThat(props).isEqualTo(props);
    }

    @Test
    void testEquals_DifferentMaps_NotEqual() {
        GithubClientProperties props1 = new GithubClientProperties();
        GithubClientProperties props2 = new GithubClientProperties();

        Map<String, ClientConfig> map1 = new HashMap<>();
        ClientConfig config1 = new ClientConfig();
        config1.setClientId("id123");
        map1.put("dev", config1);

        Map<String, ClientConfig> map2 = new HashMap<>();
        ClientConfig config2 = new ClientConfig();
        config2.setClientId("id456");
        map2.put("dev", config2);

        props1.setEnvironments(map1);
        props2.setEnvironments(map2);

        assertThat(props1).isNotEqualTo(props2);
    }

    @Test
    void testHashCode_Consistency() {
        GithubClientProperties props1 = new GithubClientProperties();
        GithubClientProperties props2 = new GithubClientProperties();

        Map<String, ClientConfig> map1 = new HashMap<>();
        ClientConfig config1 = new ClientConfig();
        config1.setClientId("id123");
        map1.put("dev", config1);

        Map<String, ClientConfig> map2 = new HashMap<>();
        ClientConfig config2 = new ClientConfig();
        config2.setClientId("id123");
        map2.put("dev", config2);

        props1.setEnvironments(map1);
        props2.setEnvironments(map2);

        assertThat(props1.hashCode()).isEqualTo(props2.hashCode());
    }

    @Test
    void testToString_ContainsKeyData() {
        GithubClientProperties props = new GithubClientProperties();

        Map<String, ClientConfig> map = new HashMap<>();
        ClientConfig config = new ClientConfig();
        config.setClientId("id123");
        map.put("dev", config);

        props.setEnvironments(map);

        String toString = props.toString();
        assertThat(toString).contains("environments");
        assertThat(toString).contains("dev");
        assertThat(toString).contains("id123");
    }

}
