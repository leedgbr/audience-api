package com.audition.common.exception;

import java.io.Serial;
import lombok.Getter;

/**
 * Represents a business error, usually due to client input not meeting a constraint.
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2730228640925849139L;

    private static final String TITLE_VALIDATION = "Validation";
    private static final String TITLE_RESOURCE_NOT_FOUND = "Resource Not Found";

    /**
     * A high level categorisation of the error.
     */
    private String title;

    /**
     * The name or jsonpath of the field this error relates to, if applicable.
     */
    private String field;

    /**
     * The detailed reason for the error, typically a reason code such as POST_NOT_FOUND.
     */
    private String detail;

    /**
     * Static factory method for creating a new BusinessException to represent a validation error.
     *
     * @param field  The name or jsonpath of the field this error relates to
     * @param detail The detailed reason for the error
     * @return A BusinessException representing this problem.
     */
    public static BusinessException newValidationError(final String field, final String detail) {
        final BusinessException e = new BusinessException();
        e.title = TITLE_VALIDATION;
        e.field = field;
        e.detail = detail;
        return e;
    }

    /**
     * Static factory method for creating a new BusinessException to represent a problem where the requested resource is
     * missing.
     *
     * @param detail The detailed reason for the error
     * @return A BusinessException representing this problem.
     */
    public static BusinessException newResourceNotFound(final String detail) {
        final BusinessException e = new BusinessException();
        e.title = TITLE_RESOURCE_NOT_FOUND;
        e.detail = detail;
        return e;
    }
}
