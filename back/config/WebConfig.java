// src/main/java/com/lms/urbangreen/urbangreenproject/config/WebConfig.java
package com.lms.urbangreen.urbangreenproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** URL -> 프로젝트 실행경로 하위 uploads/ 폴더로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
