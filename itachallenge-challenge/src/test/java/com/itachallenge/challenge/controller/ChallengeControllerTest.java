package com.itachallenge.challenge.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChallengeControllerTest {

    @Test
    public void test() {
        assertEquals(new ChallengeController().test(), "Hello from ITA Challenge!!!");
    }
}