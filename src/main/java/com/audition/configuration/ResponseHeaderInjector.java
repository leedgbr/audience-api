package com.audition.configuration;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Injects the required headers into the http response.
 */
public class ResponseHeaderInjector implements HandlerInterceptor {

    /**
     * Adds the X-Span-ID and X-Trace-ID headers into the response, taking the values from the configured tracing.
     *
     * @param request  The current HTTP request
     * @param response The current HTTP response
     * @param handler  The chosen handler to execute, for type and/or instance evaluation
     * @return True, so that processing continues.
     */
    @Override
    public boolean preHandle(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final Object handler) {

        final Tracer tracer = Tracing.currentTracer();
        if (tracer == null) {
            return true;
        }
        final Span span = tracer.currentSpan();
        if (span == null) {
            return true;
        }
        final TraceContext context = span.context();

        final String spanId = context.spanIdString();
        final String traceId = context.traceIdString();
        response.setHeader("X-Trace-ID", traceId);
        response.setHeader("X-Span-ID", spanId);
        return true;
    }
}