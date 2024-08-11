package com.audition.common.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2730228640925849139L;

    private static final String TITLE_VALIDATION = "Validation";
    private static final String TITLE_RESOURCE_NOT_FOUND = "Resource Not Found";

    private String title;
    private String field;
    private String detail;

    public static BusinessException newValidationError(final String detail) {
        final BusinessException e = new BusinessException();
        e.title = TITLE_VALIDATION;
        e.detail = detail;
        return e;
    }

    public static BusinessException newValidationError(final String field, final String detail) {
        final BusinessException e = new BusinessException();
        e.title = TITLE_VALIDATION;
        e.field = field;
        e.detail = detail;
        return e;
    }

    public static BusinessException newResourceNotFound(final String detail) {
        final BusinessException e = new BusinessException();
        e.title = TITLE_RESOURCE_NOT_FOUND;
        e.detail = detail;
        return e;
    }
}
