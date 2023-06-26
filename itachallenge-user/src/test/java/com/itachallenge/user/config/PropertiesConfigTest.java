package com.itachallenge.user.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PropertiesConfigTest {
    @Autowired(required = true)
    PropertiesConfig prpsConfig;

    @Test
    void getUrlMaxLength() {
        //region VARIABLES
        int value = 2048;

        //endregion VARIABLES


        //region TEST
        assertEquals(value, prpsConfig.getUrlMaxLength());

        //endregion TEST

    }
}