package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordFilterTest {

    @DisplayName("Test keyword filter with disallowed keyword")
    @Test
    void testKeywordFilterWithDisallowedKeyword() {
        KeywordFilter filter = new KeywordFilter();
        String disallowedCode = "import java.util.*;";
        ExecutionResult result = filter.apply(disallowedCode);
        assertFalse(result.isSuccess(), "The result should indicate a disallowed keyword");
        assertEquals("Security violation: disallowed keyword detected: import", result.getMessage(), "The message should indicate a disallowed keyword");
    }

    @DisplayName("Test keyword filter with allowed code")
    @Test
    void testKeywordFilterWithAllowedCode() {
        KeywordFilter filter = new KeywordFilter();
        String allowedCode = "System.out.println(\"Hello, World!\");";
        ExecutionResult result = filter.apply(allowedCode);
        assertEquals("Code passed keyword filter", result.getMessage(), "The message should indicate that the code passed the keyword filter");
    }
}