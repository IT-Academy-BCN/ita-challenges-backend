package com.itachallenge.challenge.helper;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import io.micrometer.common.util.StringUtils;

@Component
public class UuidValidator {

    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    	private UuidValidator() {};
    
    public static  boolean isValidUUID(String id) {
        return !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();
    }

}
