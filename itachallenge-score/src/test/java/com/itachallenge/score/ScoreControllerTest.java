package com.itachallenge.score;

import com.itachallenge.score.controller.ScoreController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class ScoreControllerTest {

    @Test
    void test() {
        assertEquals("Hello from ITA Score!!!", new ScoreController().test());
    }

}
