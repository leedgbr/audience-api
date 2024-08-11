package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audition.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidatorTest {

    public Validator validator;

    @BeforeEach
    void setUp() {
        this.validator = new Validator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"     "})
    void mandatoryPostId(final String postId) {
        final BusinessException exception = assertThrows(
            BusinessException.class, () -> validator.validate(postId)
        );
        assertEquals("Validation", exception.getTitle());
        assertEquals("id", exception.getField());
        assertEquals("POST_ID_REQUIRED", exception.getDetail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "ABC", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "+"})
    void numericPostId(final String postId) {
        final BusinessException exception = assertThrows(
            BusinessException.class, () -> validator.validate(postId)
        );
        assertEquals("Validation", exception.getTitle());
        assertEquals("id", exception.getField());
        assertEquals("POST_ID_MUST_BE_NUMERIC", exception.getDetail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12345", "99999999999999999999999999999999999999999999"})
    void validPostId(final String postId) {
        assertDoesNotThrow(() -> validator.validate(postId));
    }
}