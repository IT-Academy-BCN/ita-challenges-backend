package com.itachallenge.challenge.helper;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PaginationHelper {

    private final static int DEFAULT_PAGE_SIZE= 5;

    protected int getValidPageNumber(String pageNumber) {
        if (pageNumber == null || pageNumber.isEmpty()) {
            return 1;
        }
        if (!NumberUtils.isDigits(pageNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(pageNumber);
    }

    protected int getValidPageSize(String pageSize) {
        if (pageSize == null || pageSize.isEmpty()) {
            return DEFAULT_PAGE_SIZE;
        }
        if (!NumberUtils.isDigits(pageSize)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(pageSize);
    }

}
