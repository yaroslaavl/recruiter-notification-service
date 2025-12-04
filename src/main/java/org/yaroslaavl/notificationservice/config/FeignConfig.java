package org.yaroslaavl.notificationservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaroslaavl.notificationservice.feignClient.user.TokenInterceptor;

@Configuration
public class FeignConfig {

    private final TokenInterceptor tokenInterceptor;

    public FeignConfig(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return tokenInterceptor;
    }
}
