package com.bigfatcat.geometry3;

import com.bigfatcat.geometry3.controller.interceptor.LoginInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * 项目Web相关配置
 * 包括：静态文件访问路由，拦截器
 * */
@SpringBootConfiguration
public class Geometry3WebConfig extends WebMvcConfigurationSupport {

    @Resource(name = "loginInterceptor")
    private LoginInterceptor loginInterceptor;

    /**
     * 静态文件访问路由配置
     * 包括：static目录下文件、html模板文件
     * */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.html").addResourceLocations("classpath:/templates/");
    }

    /**
     * 拦截器配置
     * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器配置
        InterceptorRegistration loginInterceptorRegistration = registry.addInterceptor(loginInterceptor);
        loginInterceptorRegistration.addPathPatterns("/newProblem");
        loginInterceptorRegistration.addPathPatterns("/getProblemPage");
        loginInterceptorRegistration.addPathPatterns("/changeProblemPage");
    }

}
