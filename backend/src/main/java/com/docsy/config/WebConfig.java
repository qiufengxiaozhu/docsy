package com.docsy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：CORS、静态资源映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final DocsyProperties properties;

    public WebConfig(DocsyProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Vue 管理后台静态资源
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/static/admin/");

        // OnlyOffice WASM 静态资源（定制化目录优先，原始资源兜底）
        registry.addResourceHandler("/onlyoffice/**")
                .addResourceLocations(
                        properties.getOnlyofficeCustomPath(),
                        properties.getOnlyofficePath()
                );

        // 默认静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
