package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${swagger.enabled:false}")
    private boolean swaggerEnabled;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        if (swaggerEnabled) {
            registry.addResourceHandler("/swagger.html")
                .addResourceLocations("classpath:/openapi/")
                .setCachePeriod(0);
        }

        registry.addResourceHandler("/openapi.yaml")
            .addResourceLocations("classpath:/openapi/")
            .setCachePeriod(0);
    }
}
