package com.sahil.Mark9.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final ParentPinInterceptor pinInterceptor;

    public WebConfig(ParentPinInterceptor pinInterceptor) {
        this.pinInterceptor = pinInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pinInterceptor)
            .addPathPatterns("/parent/**")
            .excludePathPatterns(
                "/parent/pin",
                "/parent/pin/verify",

                // ✅ allow pin reset flow
                "/parent/pin/reset",
                "/parent/pin/reset/**",

                "/parent/logout",
                "/css/**",
                "/js/**",
                "/images/**"
            );
    }
    
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
