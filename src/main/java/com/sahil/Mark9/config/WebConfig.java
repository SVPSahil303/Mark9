package com.sahil.Mark9.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("https://flask-ml-service-1.onrender.com",
                            "http://localhost:5000",
                            "https://ml-webservice.onrender.com") // Flask port
            .allowedMethods("GET", "POST")
            .allowedHeaders("*");
    }
}
