package com.audition.common.logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

}
