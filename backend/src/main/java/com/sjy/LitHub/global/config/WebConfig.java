package com.sjy.LitHub.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/gen/userGenFile/**")
			.addResourceLocations("file:///" + AppConfig.getFileUploadDir() + "/gen/userGenFile/");

		registry.addResourceHandler("/gen/postGenFile/**")
			.addResourceLocations("file:///" + AppConfig.getFileUploadDir() + "/gen/postGenFile/");
	}
}