package com.audition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Custom configuration for spring web mvc.
 */
@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    /**
     * Configures the jackson ObjectMapper to serialise and deserialize objects to json in the expected manner.
     *
     * @return The configured ObjectMapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        // TODO configure Jackson Object mapper that
        //  1. allows for date format as yyyy-MM-dd - DONE - NOT YET EXPLICITLY TESTED
        //  2. Does not fail on unknown properties - DONE - NOT YET EXPLICITLY TESTED
        //  3. maps to camelCase - DONE - NOT YET EXPLICITLY TESTED
        //  4. Does not include null values or empty values - DONE - NOT YET EXPLICITLY TESTED
        //  5. does not write datas as timestamps - DONE AS PART OF 1. - NOT YET EXPLICITLY TESTED
        return new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            .setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault()))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Configures the RestTemplate for use when communicating via http with collaborating services.
     *
     * @param builder                  The spring boot initialised RestTemplateBuilder
     * @param clientHttpRequestFactory The currently configured request factory
     * @return The configured RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder,
        final ClientHttpRequestFactory clientHttpRequestFactory) {
        return builder
            .requestFactory(() -> clientHttpRequestFactory)
            .build();
        // TODO create a logging interceptor that logs request/response for rest template calls.
    }

    /**
     * Configures the request factory to buffer reading of http response streams.
     *
     * @param httpClient The currently configured http client
     * @return The configured ClientHttpRequestFactory.
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(final HttpClient httpClient) {
        return new BufferingClientHttpRequestFactory(
            new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    /**
     * Configures the apache http client with the desired timeouts and connection pool.
     *
     * @param requestConfig                      The RequestConfig which includes the desired timeouts and other request
     *                                           settings
     * @param poolingHttpClientConnectionManager The already configured connection manager implementation which uses a
     *                                           connection pool
     * @return The configured HttpClient.
     */
    @Bean
    public HttpClient httpClient(final RequestConfig requestConfig,
        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        return HttpClientBuilder.create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    /**
     * Sets the desired timeouts - from externalised configuration - on the apache http client request configuration.
     *
     * @param connectionRequestTimeout The desired timeout when requesting a connection from the connection manager
     * @param connectTimeout           The desired timeout when establishing a connection
     * @param responseTimeout          The desired timeout when waiting for a http response
     * @return The RequestConfig
     */
    @Bean
    public RequestConfig requestConfig(
        @Value("${httpclient.connection-request-timeout-in-millis}") final int connectionRequestTimeout,
        @Value("${httpclient.connect-timeout-in-millis}") final int connectTimeout,
        @Value("${httpclient.response-timeout-in-millis}") final int responseTimeout) {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.MILLISECONDS)
            .setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .setResponseTimeout(responseTimeout, TimeUnit.MILLISECONDS)
            .build();
    }

    /**
     * Configures an apache connection manager that implements connection pooling.
     *
     * @param maxTotal           The maximum total number of connections to be kept in the pool
     * @param defaultMaxPerRoute The number of connections allowed to be used for each http host connected to
     * @return The configured PoolingHttpClientConnectionManager.
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(
        @Value("${httpclient.connection-pool.max-total}") final int maxTotal,
        @Value("${httpclient.connection-pool.default-max-per-route}") final int defaultMaxPerRoute) {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return connectionManager;
    }

    /**
     * Configures the response header injector on all routes.
     *
     * @param registry The spring interceptor registry.
     */
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry
            .addInterceptor(new ResponseHeaderInjector())
            .addPathPatterns("/**");
    }
}
