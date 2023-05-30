package com.itachallenge.challenge.config;

import com.itachallenge.challenge.helpers.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PropertiesConfigTest {

    @Autowired
    private PropertiesConfig config;

    @Test
    @DisplayName("Get challenge's filters available from properties Test")
    @SneakyThrows(IOException.class)
    void loadFiltersDataTest(){
        String jsonPath = "json/FiltersInfoResponse.json";
        String expected =  new ResourceHelper(jsonPath).readResourceAsString();

        MockedConstruction.MockInitializer<ResourceHelper> configureResourceHelperMock =
                (mock, context) ->
                        when(mock.readResourceAsString()).thenReturn(expected);

        try (MockedConstruction<ResourceHelper> mockedResourceHelper =
                     mockConstruction(ResourceHelper.class,
                             configureResourceHelperMock )) {
            assertThat(config.loadFiltersData()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("Get challenge's sorting options available from properties Test")
    @SneakyThrows(IOException.class)
    void loadSortDataTest(){
        String jsonPath = "json/SortInfoResponse.json";
        String expected =  new ResourceHelper(jsonPath).readResourceAsString();

        MockedConstruction.MockInitializer<ResourceHelper> configureResourceHelperMock =
                (mock, context) ->
                        when(mock.readResourceAsString()).thenReturn(expected);

        try (MockedConstruction<ResourceHelper> mockedResourceHelper =
                     mockConstruction(ResourceHelper.class,
                             configureResourceHelperMock )) {
            assertThat(config.loadSortData()).isEqualTo(expected);
        }
    }
}
