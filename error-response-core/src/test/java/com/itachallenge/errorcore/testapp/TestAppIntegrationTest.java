package com.itachallenge.errorcore.testapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TestAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // 1️⃣ MethodArgumentTypeMismatchException
    @Test
    void shouldHandleTypeMismatch() throws Exception {
        mockMvc.perform(get("/test/mismatch").param("age", "notANumber"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Parameter")))
                .andExpect(jsonPath("$.errors[0].field", is("age")));
    }

    // 2️⃣ ConstraintViolationException
    @Test
    void shouldHandleConstraintViolation() throws Exception {
        mockMvc.perform(get("/test/mismatch").param("age", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("validation")));
    }

    // 3️⃣ MethodArgumentNotValidException
    @Test
    void shouldHandleInvalidBody() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.errors").isArray());
    }

    // 4️⃣ IllegalArgumentException
    @Test
    void shouldHandleIllegalArgument() throws Exception {
        mockMvc.perform(get("/test/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Invalid parameter")));
    }

    // 5️⃣ ResponseStatusException
    @Test
    void shouldHandleResponseStatusException() throws Exception {
        mockMvc.perform(get("/test/status"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("404 NOT_FOUND \"Resource not found\"")));
    }

    // 6️⃣ Generic Exception
    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/any"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("terribly wrong")));
    }
}
