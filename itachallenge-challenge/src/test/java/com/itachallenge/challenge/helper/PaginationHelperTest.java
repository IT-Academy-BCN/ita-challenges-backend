package com.itachallenge.challenge.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationHelperTest {

    @DisplayName("Test get valid pageNumber and pageSize")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void getValidPageNumberTest_ValidPageNumber(String validPageNumber) {
        PaginationHelper paginationHelper = new PaginationHelper();
        Optional<Integer> result = paginationHelper.getValidPageNumber(validPageNumber);
        assertEquals(Optional.of(1), result);
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"","a","0","-1","1.1"})
    void getValidPageNumberTest_NotValidPageNumber(String notValidPageNumber) {
        PaginationHelper paginationHelper = new PaginationHelper();
        Optional<Integer> result = paginationHelper.getValidPageNumber(notValidPageNumber);
        assertEquals(Optional.empty(), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3"})
    void getValidPageSizeTest_ValidPageSize(String validPageSize) {
        PaginationHelper paginationHelper = new PaginationHelper();
        Optional<Integer> result = paginationHelper.getValidPageSize(validPageSize);
        assertEquals(Optional.of(3), result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"","a","0","-1","1.1"})
    void getValidPageSizeTest_NotValidPageSize(String notValidPageSize) {
        PaginationHelper paginationHelper = new PaginationHelper();
        Optional<Integer> result = paginationHelper.getValidPageSize(notValidPageSize);
        assertEquals(Optional.empty(), result);
    }

}