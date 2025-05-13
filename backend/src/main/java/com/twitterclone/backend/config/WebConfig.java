package com.twitterclone.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:///C:/dev/twitter-clone/uploads/";

        registry
                .addResourceHandler("/uploads/avatars/**")
                .addResourceLocations(uploadPath + "avatars/");
    }
}
