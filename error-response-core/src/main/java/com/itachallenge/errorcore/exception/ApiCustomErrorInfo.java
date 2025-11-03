package com.itachallenge.errorcore.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public record ApiCustomErrorInfo(HttpStatus status, String messageKey, Object[] messageArgs )  {
    public static ApiCustomErrorInfo of(HttpStatus status, String messageKey, Object[] messageArgs){
        return new ApiCustomErrorInfo(status,messageKey,messageArgs);
    }
    // ✅ equals() — use Arrays.equals for array fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiCustomErrorInfo(HttpStatus status1, String key, Object[] args))) return false;
        return status == status1 &&
                Objects.equals(messageKey, key) &&
                Arrays.equals(messageArgs, args);
    }

    // ✅ hashCode() — use Arrays.hashCode for array fields
    @Override
    public int hashCode() {
        int result = Objects.hash(status, messageKey);
        result = 31 * result + Arrays.hashCode(messageArgs);
        return result;
    }

    // ✅ toString() — use Arrays.toString for array fields
    @Override
    public String toString() {
        return "ApiCustomErrorInfo{" +
                "status=" + status +
                ", messageKey='" + messageKey + '\'' +
                ", messageArgs=" + Arrays.toString(messageArgs) +
                '}';
    }
}
