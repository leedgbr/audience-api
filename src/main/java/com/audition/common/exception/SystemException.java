package com.audition.common.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5876728854007114881L;

    private Integer statusCode;

    public SystemException(final String message, final Throwable exception) {
        super(message, exception);
    }

    public SystemException(final String message, final Integer errorCode) {
        super(message);
        this.statusCode = errorCode;
    }

    @Override
    public String toString() {
        return String.format("[message]= %s, [statusCode]= %s", this.getMessage(), this.getStatusCode());
    }
}
