package com.itachallenge.challenge.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationHelperTest {

    @Test
    @DisplayName("Validate pagination parameters")

    public void getValidPageNumberTest_PageNumberNull() {
        PaginationHelper paginationHelper = new PaginationHelper();
        int result = paginationHelper.getValidPageNumber(null);
        assertEquals(1, result);
    }

    @Test
    void getValidPageNumberTest_PageNumberEmpty() {
        PaginationHelper paginationHelper = new PaginationHelper();
        int result = paginationHelper.getValidPageNumber("");
        assertEquals(1, result);
    }
    @Test
    void getValidPageNumberTest_PageNumberZero() {
        PaginationHelper paginationHelper = new PaginationHelper();
        int result = paginationHelper.getValidPageNumber("0");
        assertEquals(1, result);
    }
    @Test
    void getValidPageNumberTest_NonNumericPageNumber_ThrowsBadRequestException() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String nonNumericPageNumber = "a";
        assertThrows(ResponseStatusException.class, () -> paginationHelper.getValidPageNumber(nonNumericPageNumber));
    }

    @Test
    void getValidPageNumberTest_NegativePageNumber_ThrowsBadRequestException() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String negativePageNumber = "-1";
        assertThrows(ResponseStatusException.class, () -> paginationHelper.getValidPageNumber(negativePageNumber));
    }

    @Test
    void getValidPageNumberTest_DoublePageNumber_ThrowsBadRequestException() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String doublePageNumber = "1.1";
        assertThrows(ResponseStatusException.class, () -> paginationHelper.getValidPageNumber(doublePageNumber));
    }

    @Test
    void getValidPageNumberTest_ValidPageNumber() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String expectedPageNumber = "3";
        int result = paginationHelper.getValidPageNumber(expectedPageNumber);
        assertEquals(3, result);
    }

    @Test
    void getValidPageSizeTest_PageSizeNull() {
        PaginationHelper paginationHelper = new PaginationHelper();
        int result = paginationHelper.getValidPageSize(null);
        assertEquals(5, result);
    }

    @Test
    void getValidPageSizeTest_PageSizeEmpty() {
        PaginationHelper paginationHelper = new PaginationHelper();
        int result = paginationHelper.getValidPageSize("");
        assertEquals(5, result);
    }
    @Test
    void getValidPageNumberTest_PageSizeZero() {
        PaginationHelper paginationHelper = new PaginationHelper();
        int result = paginationHelper.getValidPageSize("0");
        assertEquals(5, result);
    }
    @Test
    void getValidPageSizeTest_NonNumericPageSize_ThrowsBadRequestException() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String nonNumericPageSize = "a";
        assertThrows(ResponseStatusException.class, () -> paginationHelper.getValidPageSize(nonNumericPageSize));
    }

    @Test
    void getValidPageSizeTest_NegativePageSize_ThrowsBadRequestException() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String negativePageSize = "-1";
        assertThrows(ResponseStatusException.class, () -> paginationHelper.getValidPageNumber(negativePageSize));
    }

    @Test
    void getValidPageSizeTest_DoublePageSize_ThrowsBadRequestException() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String doublePageSize = "1.1";
        assertThrows(ResponseStatusException.class, () -> paginationHelper.getValidPageNumber(doublePageSize));
    }

    @Test
    void getValidPageSizeTest_ValidPageSize() {
        PaginationHelper paginationHelper = new PaginationHelper();
        String expectedPageSize = "10";
        int result = paginationHelper.getValidPageNumber(expectedPageSize);
        assertEquals(10, result);
    }
}