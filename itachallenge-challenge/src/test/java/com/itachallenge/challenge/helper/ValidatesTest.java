package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.config.PropertiesConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ValidatesTest {

    @Mock
    private PropertiesConfig propertiesConfig;
    @InjectMocks
    private Validates validates;

    @BeforeEach
    public void setUp() {
        // Inicializar los mocks anotados con @Mock
        MockitoAnnotations.openMocks(this);

        // Configurar los valores de retorno para los métodos en el mock de propertiesConfig
        when(propertiesConfig.getEasy()).thenReturn("Easy");
        when(propertiesConfig.getMedium()).thenReturn("Medium");
        when(propertiesConfig.getHard()).thenReturn("Hard");
        when(propertiesConfig.getJava()).thenReturn("Java");
        when(propertiesConfig.getJavascript()).thenReturn("Javascript");
        when(propertiesConfig.getPhp()).thenReturn("PHP");
        when(propertiesConfig.getPython()).thenReturn("Python");
    }

    @Test
    void isValidLevel_True_test() {
        assertTrue(validates.isValidLevel("EASY"));
    }

    @Test
    void isValidLevel_False_test() {
        assertFalse(validates.isValidLevel("Not_level"));
        assertFalse(validates.isValidLevel(null));
    }

    @Test
    void isValidLanguage_True_test() {
        assertTrue(validates.isValidLanguage("Java"));
    }

    @Test
    void isValidLanguage_False_test() {
        assertFalse(validates.isValidLanguage("Not_language"));
        assertFalse(validates.isValidLanguage(null));
    }

    @Test
    public void validLenguageLevel_test() {
        Set<String> validLevels = new HashSet<>(Set.of("Easy", "Medium", "Hard"));
        Set<String> validLanguages = new HashSet<>(Set.of("Java", "Python", "PHP", "Javascript"));

        assertDoesNotThrow(() -> validates.validLenguageLevel(validLevels, validLanguages));
    }

}
