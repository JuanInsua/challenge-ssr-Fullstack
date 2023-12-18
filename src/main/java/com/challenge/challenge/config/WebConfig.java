package com.challenge.challenge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/spotify/me")
                .allowedOrigins("http://localhost:8080") // Reemplaza con el dominio de tu aplicación
                .allowedMethods("GET")
                .allowCredentials(true);
    }
}

