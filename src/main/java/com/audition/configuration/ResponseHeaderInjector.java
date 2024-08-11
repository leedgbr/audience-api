package com.audition.configuration;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class ResponseHeaderInjector implements HandlerInterceptor {

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