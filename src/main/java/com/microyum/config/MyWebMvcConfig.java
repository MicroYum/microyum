package com.microyum.config;

import com.microyum.common.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(Constants.BLOG_FILE_VIRTUAL_PICTURE_DIR + "**").addResourceLocations("file:" + Constants.BLOG_FILE_ACTUAL_PICTURE_DIR);
        registry.addResourceHandler(Constants.ALBUM_FILE_VIRTUAL_PICTURE_DIR + "**").addResourceLocations("file:" + Constants.ALBUM_FILE_ACTUAL_PICTURE_DIR);
    }
}
