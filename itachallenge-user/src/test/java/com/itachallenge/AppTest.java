package com.itachallenge;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void main_runs() {
        App.main(new String[]{"--spring.main.web-application-type=none"});
    }
}