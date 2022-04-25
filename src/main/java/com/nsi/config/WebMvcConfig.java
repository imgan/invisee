package com.nsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.nsi.interceptor.NeedLoginInterceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter{
	

	@Bean
	public NeedLoginInterceptor getLoginInterceptor() {
	    return new NeedLoginInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.getLoginInterceptor())
//		.addPathPatterns("/agent/commission/*")
//		.addPathPatterns("/agent/subordinate/*")
//		.excludePathPatterns("/agent/commission/calculate");
		.addPathPatterns("/*")
		.addPathPatterns("/*/*")
		.addPathPatterns("/*/*/*")
		.addPathPatterns("/*/*/*/*")
		.excludePathPatterns("/error")
		.excludePathPatterns("/agent/login")
		.excludePathPatterns("/agent/failed");
	}

}
