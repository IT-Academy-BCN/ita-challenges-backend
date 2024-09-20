package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlSanitizerFilterTest {

    @DisplayName("Test sanitizing valid HTML")
    @Test
    void testSanitizeValidHtml() {
        HtmlSanitizerFilter filter = new HtmlSanitizerFilter();
        String validHtml = "<p>This is a paragraph.</p>";
        String expectedValidHtml = "This is a paragraph.";
        String sanitizedHtml = filter.sanitize(validHtml);
        assertEquals(expectedValidHtml, sanitizedHtml, "The sanitized HTML should be equal to the valid HTML");
    }

    @DisplayName("Test sanitizing HTML with dangerous tags")
    @Test
    void testSanitizeHtmlWithDangerousTags() {
        HtmlSanitizerFilter filter = new HtmlSanitizerFilter();
        String dangerousHtml = "<script>alert('XSS');</script><p>This is a paragraph.</p>";
        String expectedSanitizedHtml = "This is a paragraph.";
        String sanitizedHtml = filter.sanitize(dangerousHtml);
        assertEquals(expectedSanitizedHtml, sanitizedHtml, "The sanitized HTML should not contain dangerous tags");
    }

    @DisplayName("Test sanitizing empty HTML")
    @Test
    void testSanitizeEmptyHtml() {
        HtmlSanitizerFilter filter = new HtmlSanitizerFilter();
        String emptyHtml = "";
        String sanitizedHtml = filter.sanitize(emptyHtml);
        assertEquals(emptyHtml, sanitizedHtml, "The sanitized HTML should be equal to the empty HTML");
    }

    @DisplayName("Test sanitizing null HTML")
    @Test
    void testSanitizeNullHtml() {
        HtmlSanitizerFilter filter = new HtmlSanitizerFilter();
        ExecutionResult result = filter.apply(null);
        String sanitizedHtml = filter.sanitize(null);
        assertEquals("", sanitizedHtml, "The sanitized HTML should be an empty string");
        assertEquals("Code is null", result.getMessage(), "The message should be 'Code is null'");



    }
}