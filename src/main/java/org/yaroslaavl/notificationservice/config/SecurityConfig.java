package org.yaroslaavl.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@EnableAsync
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtToken -> {
                                    Collection<GrantedAuthority> authorities = new KeyCloakAuthenticationRoleConverter().convert(jwtToken);
                                    return new JwtAuthenticationToken(jwtToken, authorities);
                                })
                        )
                );
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(
                                        "/error",
                                        "/actuator/health",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/api/v1/mail/check",
                                        "/api/v1/mail/request-verification-candidate",
                                        "/api/v1/mail/request-verification-recruiter",
                                        "/api/v1/mail/verify-code").permitAll()
                                .requestMatchers("/api/v1/mail/mine-notifications").authenticated());
        return http.build();
    }
}
