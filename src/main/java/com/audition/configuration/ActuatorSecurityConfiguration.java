package com.audition.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ActuatorSecurityConfiguration {

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