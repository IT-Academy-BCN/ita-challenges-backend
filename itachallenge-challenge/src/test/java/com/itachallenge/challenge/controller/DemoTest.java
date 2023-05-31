package com.itachallenge.challenge.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemoTest {

    @Test
    void demoTest(){
        Demo demo = new Demo();
        assertEquals("test",demo.demo());
    }

    @Test
    void demoSumTest(){
        Demo demo = new Demo();
        assertEquals(2,demo.demoSum(1,1));
    }
}
