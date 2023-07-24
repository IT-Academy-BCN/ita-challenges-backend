package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.exception.ParameterNotValidException;
import com.itachallenge.challenge.service.ChallengeServiceImp;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaginationHelper {

    private static final int DEFAULT_PAGE_SIZE = 5;

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImp.class);

    public int getValidPageNumber(String pageNumber) {
        if (pageNumber == null || pageNumber.isEmpty() || pageNumber.equals("0")) {
            return 1;
        }
        if (!NumberUtils.isDigits(pageNumber)) {
            log.warn("Parameter not valid: {}", pageNumber);
            throw new ParameterNotValidException("Parameter not valid. Must be an integer and positive number.");
        }
        return Integer.parseInt(pageNumber);
    }

    public int getValidPageSize(String pageSize) {
        if (pageSize == null || pageSize.isEmpty() || pageSize.equals("0")) {
            return DEFAULT_PAGE_SIZE;
        }
        if (!NumberUtils.isDigits(pageSize)) {
            log.warn("Parameter not valid: {}", pageSize);
            throw new ParameterNotValidException("Parameter not valid. Must be an integer and positive number.");
        }
        return Integer.parseInt(pageSize);
    }

}



