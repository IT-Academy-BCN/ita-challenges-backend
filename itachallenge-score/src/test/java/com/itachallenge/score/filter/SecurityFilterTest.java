package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityFilterTest {

    @Autowired
    @Qualifier("securityFilter")
    private SecurityFilter filter;

    @DisplayName("Test security filter with harmful code")
    @Test
    void testSecurityFilterWithHarmfulCode() {
        String harmfulCode = "System.exit(0);";
        ExecutionResult result = filter.apply(harmfulCode);
        assertFalse(result.isSuccess(), "The result should indicate a security violation");
        assertEquals("Security violation: potentially harmful code detected: System.exit", result.getMessage(), "The message should indicate a security violation");
    }

    @DisplayName("Test security filter with safe code")
    @Test
    void testSecurityFilterWithSafeCode() {
        String safeCode = "System.out.println(\"Hello, World!\");";
        ExecutionResult result = filter.apply(safeCode);
        assertTrue(result.isSuccess(), "The result should indicate the code is safe");
        assertEquals("Code passed security filter", result.getMessage(), "The message should indicate that the code passed the security filter");
    }
}