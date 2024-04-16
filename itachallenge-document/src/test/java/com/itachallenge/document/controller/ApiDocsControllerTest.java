package com.itachallenge.document.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiDocsControllerTest {

    @Test
    void testGetApiDocs() throws Exception {

        ApiDocsController apiDocsController = new ApiDocsController();

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(apiDocsController).build();

        mockMvc.perform(get("/api-docs/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api-docs"));
    }
}
