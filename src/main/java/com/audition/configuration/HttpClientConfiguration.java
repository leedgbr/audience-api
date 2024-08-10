package com.audition.configuration;

import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class HttpClientConfiguration {

    @Value("${audition-source-url}")
    public String auditionSourceUrl;

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public HttpClient httpClient(RequestConfig requestConfig,
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        return HttpClientBuilder.create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    @Bean
    public RequestConfig requestConfig(
        @Value("${httpclient.connection-request-timeout-in-millis}") int connectionRequestTimeout,
        @Value("${httpclient.connect-timeout-in-millis}") int connectTimeout,
        @Value("${httpclient.response-timeout-in-millis}") int responseTimeout) {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.MILLISECONDS)
            .setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .setResponseTimeout(responseTimeout, TimeUnit.MILLISECONDS)
            .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(
        @Value("${httpclient.connection-pool.max-total}") int maxTotal,
        @Value("${httpclient.connection-pool.default-max-per-route}") int defaultMaxPerRoute) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return connectionManager;
    }
}

