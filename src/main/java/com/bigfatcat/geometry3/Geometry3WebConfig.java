package com.bigfatcat.geometry3;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 项目Web相关配置
 * 包括：静态文件访问路由
 * */
@SpringBootConfiguration
public class Geometry3WebConfig extends WebMvcConfigurationSupport {

    /**
     * 静态文件访问路由配置
     * 包括：static目录下文件、html模板文件
     * */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.html").addResourceLocations("classpath:/templates/");
    }

}
