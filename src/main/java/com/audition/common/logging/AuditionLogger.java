package com.audition.common.logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Entrypoint to logging for use across the application whenever there is a need to write to the log.  Note that
 * exceptions are typically expected to be bubbled up and logged in one place at the top level.
 */
@Component
public class AuditionLogger {

    /**
     * Logs an ERROR with the provided message.  The exception provided can be used to output a stack trace.
     *
     * @param logger  The logger to log to.
     * @param message The message to log at ERROR level.
     * @param e       The related exception, if applicable.
     */
    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

}
