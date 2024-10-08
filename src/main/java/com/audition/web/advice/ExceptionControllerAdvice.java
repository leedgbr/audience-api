package com.audition.web.advice;

import com.audition.common.exception.BusinessException;
import com.audition.common.logging.AuditionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * Handles exceptions at the top level to ensure they are mapped to an appropriate response and logged where indicated.
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private static final String TITLE_DEFAULT = "API Error Occurred";
    private static final String MESSAGE_DEFAULT = "API Error occurred. Please contact support or administrator.";
    private static final String PROPERTY_NAME_FIELD = "field";

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Autowired
    private AuditionLogger logger;

    /**
     * Maps a business error to the standard response format.
     *
     * @param e The exception that occurred
     * @return The ProblemDetail representing the standard http response.
     */
    @ExceptionHandler(BusinessException.class)
    ProblemDetail handleBusinessException(final BusinessException e) {
        // Don't necessarily need to log this as it is a client problem.

        // Also, I've left it such that all business exceptions will return a 422 error code, which I believe is
        // sufficient.  Sometimes organisations have a standard whereby requesting a resource by id that does not exist
        // would return a 404 error code.  This can also be considered a business exception, but would require a little
        // more work to make happen here.  It's a viable option, I just haven't gone that far for the purposes of this.
        return createProblemDetail(e);
    }

    /**
     * Maps any error other than a BusinessException to the standard response format.  This is treated as a system error
     * and so is also logged as an error.
     *
     * @param e The exception that occurred
     * @return The ProblemDetail representing the standard http response.
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail handleException(final Exception e) {
        // If it's not a specific business error, just log it and return default system error response.  We will see
        // it in the logs and deal with it appropriately at the time if it is a case we have not yet covered.
        logger.logErrorWithException(LOG, "A system error occurred: " + e, e);
        return createProblemDetail();
    }

    /**
     * Creates a new problem detail for a system error.  This is a blanket response that contains boilerplate content so
     * as not to expose any internal details to clients.
     *
     * @return The ProblemDetail.
     */
    private ProblemDetail createProblemDetail() {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle(TITLE_DEFAULT);
        problemDetail.setDetail(MESSAGE_DEFAULT);
        return problemDetail;
    }


    /**
     * Creates a new problem detail for the provided business error.  This response will contain the details of the
     * constraint violated so the user will have feedback to enable them to amend their request.
     *
     * @param e The exception that occurred
     * @return The ProblemDetail.
     */
    private ProblemDetail createProblemDetail(final BusinessException e) {
        // Use 400 if that is what the corporate standards dictate, but 422 allows for differentiation between
        // malformed requests vs business rule violations.  This can help with observability.
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getDetail());
        problemDetail.setProperty(PROPERTY_NAME_FIELD, e.getField());
        return problemDetail;
    }
}



