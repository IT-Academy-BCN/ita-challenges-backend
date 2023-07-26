package com.itachallenge.challenge.helper;

import io.micrometer.common.util.StringUtils;

import java.util.Optional;

public class PaginationHelper {

    private static final java.util.regex.Pattern POSITIVE_INTEGER_FORM = java.util.regex.Pattern.compile("^[1-9]+$");

    public Optional<Integer> getValidPageNumber(String pageNumber) {
        return (!StringUtils.isEmpty(pageNumber) && POSITIVE_INTEGER_FORM.matcher(pageNumber).matches())
                ? Optional.of(Integer.parseInt(pageNumber)) : Optional.empty();
    }

    public Optional<Integer> getValidPageSize(String pageSize) {
        return (!StringUtils.isEmpty(pageSize) && POSITIVE_INTEGER_FORM.matcher(pageSize).matches())
                ? Optional.of(Integer.parseInt(pageSize)) : Optional.empty();
    }

}



