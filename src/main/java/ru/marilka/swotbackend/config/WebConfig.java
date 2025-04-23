package ru.marilka.swotbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // разрешить все маршруты
                .allowedOrigins("http://localhost:9000") // или "*" для всех источников
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // если ты используешь токены/куки
    }
}

