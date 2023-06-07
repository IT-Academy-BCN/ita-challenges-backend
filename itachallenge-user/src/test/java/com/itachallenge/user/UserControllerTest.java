package com.itachallenge.user;

import com.itachallenge.user.controller.UserController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class UserControllerTest {

    @Test
    void test() {
        assertEquals("Hello from ITA User!!!", new UserController().test());
    }

}
