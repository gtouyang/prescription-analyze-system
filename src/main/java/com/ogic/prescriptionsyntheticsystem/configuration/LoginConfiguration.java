package com.ogic.prescriptionsyntheticsystem.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//@Configuration
//public class LoginConfiguration implements WebMvcConfigurer {
//
//    @Autowired
//    HandlerInterceptor handlerInterceptor;
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(handlerInterceptor).addPathPatterns("/**")
//                .excludePathPatterns("/","/login","/login", "/**.js", "/**.css");
//    }
//
//}
