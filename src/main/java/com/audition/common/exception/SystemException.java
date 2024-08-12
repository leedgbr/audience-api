package com.audition.common.exception;

import java.io.Serial;
import lombok.Getter;

/**
 * Represents a system error, usually due to some unexpected system problem and not the fault of the user or client.
 * Often but not always retry-able.
 */
@Getter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5876728854007114881L;

    /**
     * The related http status code received from a collaborating service, if applicable.
     */
    private Integer statusCode;

    /**
     * * Static factory method for creating a new SystemException to represent a system problem.
     *
     * @param message   A message describing the detail of the system problem
     * @param exception The related exception received, if applicable
     */
    public SystemException(final String message, final Throwable exception) {
        super(message, exception);
    }

    /**
     * * Static factory method for creating a new SystemException to represent a system problem.
     *
     * @param message    A message describing the detail of the system problem
     * @param statusCode The related http status code received, if applicable
     */
    public SystemException(final String message, final Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Implementation of the toString() interface which will determine how this exception is output when serialised to a
     * string.
     *
     * @return A string representation of this error.
     */
    @Override
    public String toString() {
        return String.format("[message]= %s, [statusCode]= %s", this.getMessage(), this.getStatusCode());
    }
}
