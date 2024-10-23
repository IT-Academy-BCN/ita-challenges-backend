package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class KeywordFilterTest {

    @Autowired
    @Qualifier("keywordFilter")
    private KeywordFilter filter;

    @DisplayName("Test keyword filter with disallowed keyword")
    @Test
    void testKeywordFilterWithDisallowedKeyword() {
        String disallowedCode = "import java.util.*;";
        ExecutionResult result = filter.apply(disallowedCode);
        assertFalse(result.isSuccess(), "The result should indicate a disallowed keyword");
        assertEquals("Security violation: disallowed keyword detected: import", result.getMessage(), "The message should indicate a disallowed keyword");
    }

    @DisplayName("Test keyword filter with allowed code")
    @Test
    void testKeywordFilterWithAllowedCode() {
        String allowedCode = "System.out.println(\"Hello, World!\");";
        ExecutionResult result = filter.apply(allowedCode);

        assertEquals("Code passed security filter", result.getMessage(), "The message should indicate that the code passed the keyword filter");
    }
}