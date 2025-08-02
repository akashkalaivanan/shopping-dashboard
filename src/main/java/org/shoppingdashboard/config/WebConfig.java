package org.shoppingdashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the external folder to the URL path
        registry.addResourceHandler("/images/**","/logo/**")
                .addResourceLocations("file:C:/Users/dell/Desktop/shop/Shopping-dashboard/images/")
                .addResourceLocations("file:C:/Users/dell/Desktop/shop/Shopping-dashboard/logo/");
    }
}