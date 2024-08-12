package com.audition.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures a basic level of spring security such that only the expected endpoints are exposed.
 */
@Configuration
public class ActuatorSecurityConfiguration {

    /**
     * Applies the principle of least privilege to ensure only expected endpoints are exposed.
     *
     * @param http Spring security configuration
     * @return The configured SecurityFilterChain.
     * @throws Exception If a problem occurs when configuring spring security.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/posts", "/posts/*", "/posts/*/comments").permitAll()
                .requestMatchers("/**").authenticated()
                .and()
            );
        return http.build();
    }

}