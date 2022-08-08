package io.github.xpakx.habitgamification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String frontend;

    public WebConfig(@Value("${frontend.host}") String frontend) {
        super();
        this.frontend = frontend;
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost:4200", frontend)
                .allowedMethods("GET", "POST", "DELETE", "PUT");
    }
}