package com.audition.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private static final String TITLE_VALIDATION = "Validation";
    private static final String TITLE_RESOURCE_NOT_FOUND = "Resource Not Found";

    private String title;
    private String detail;

    public static BusinessException newValidationError(String detail) {
        BusinessException e = new BusinessException();
        e.title = TITLE_VALIDATION;
        e.detail = detail;
        return e;
    }

    public static BusinessException newResourceNotFound(String detail) {
        BusinessException e = new BusinessException();
        e.title = TITLE_RESOURCE_NOT_FOUND;
        e.detail = detail;
        return e;
    }
}
