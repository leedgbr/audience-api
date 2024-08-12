package com.audition.service;

import com.audition.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Responsible for ensuring user input meets expected business constraints.
 */
@Component
public class Validator {

    private static final String POST_ID_REQUIRED = "POST_ID_REQUIRED";
    private static final String POST_ID_MUST_BE_NUMERIC = "POST_ID_MUST_BE_NUMERIC";

    /**
     * Validates the provided audition post id.
     *
     * @param postId The id to validate.
     * @throws BusinessException If the postId is null, empty, blank or non-numeric.
     */
    public void validate(final String postId) {
        if (postId == null || postId.isBlank()) {
            throw BusinessException.newValidationError("id", POST_ID_REQUIRED);
        }
        if (!StringUtils.isNumeric(postId)) {
            throw BusinessException.newValidationError("id", POST_ID_MUST_BE_NUMERIC);
        }
    }
}
